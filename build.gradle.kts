import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

plugins {
    id("signing")
    alias(libs.plugins.outlet)
}

signing {
    useGpgCmd()
}

val mod_version: String by project

fun Project.releaseGithub(jars: List<File>) {
    val githubReleaseDir = project.layout.buildDirectory.dir("release").get().dir("github")
    githubReleaseDir.asFile.deleteRecursively()
    githubReleaseDir.asFile.mkdirs()
    println("Moving all jars to ${githubReleaseDir.asFile.absolutePath}.")

    val filesToZip = mutableListOf<File>()
    val jarWithSignature = mutableMapOf<File, File>()

    for (jar in jars) {
        val copiedJar = jar.copyTo(githubReleaseDir.asFile.resolve(jar.name), true)
        val signature = signing.sign(copiedJar).signatureFiles.first()

        val loaderDir = githubReleaseDir.dir(getLoaderFromJarName(jar.name)).asFile
        loaderDir.mkdirs()
        val loaderCopiedJar = copiedJar.copyTo(loaderDir.resolve(jar.name), true)
        val loaderSignature = signature.copyTo(loaderDir.resolve(signature.name), true)

        filesToZip += loaderCopiedJar
        filesToZip += loaderSignature

        jarWithSignature[loaderCopiedJar] = loaderSignature
    }

    val globalVersionFile = githubReleaseDir.file("versions.txt").asFile

    for (loader in githubReleaseDir.asFile.listFiles()!!.filter { it.isDirectory }.sortedBy { it.name }) {
        val loaderZipFile = githubReleaseDir.asFile.resolve("$name-$mod_version-${loader.name}.zip")
        val versionFile = loader.resolve("versions.txt")

        ZipOutputStream(BufferedOutputStream(FileOutputStream(loaderZipFile))).use { zos ->
            zos.setLevel(Deflater.BEST_COMPRESSION)
            for (file in loader.listFiles()!!.sortedBy { it.name }) {
                addToZip(file, loader, zos)
                if (file.extension == "jar") {
                    versionFile.appendText("${file.name}: ${getVersionRangeFromJarName(file.name)}\n")
                }
            }

            val signature = signing.sign(versionFile).signatureFiles.first()
            addToZip(versionFile, loader, zos)
            addToZip(signature, loader, zos)
        }
        signing.sign(loaderZipFile)

        globalVersionFile.appendText(versionFile.readText(Charsets.UTF_8))
    }

    filesToZip += globalVersionFile
    filesToZip += signing.sign(globalVersionFile).signatureFiles.first()

    val zipFile = githubReleaseDir.asFile.resolve("$name-$mod_version.zip")
    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
        zos.setLevel(Deflater.BEST_COMPRESSION)
        for (file in filesToZip) {
            addToZip(file, githubReleaseDir.asFile, zos)
        }
    }
    // let's sign the zipfiles 'cuz why not
    val zipFileSignature = signing.sign(zipFile)

//    githubReleaseDir.asFile.listFiles()!!.filter { it.isDirectory }.forEach { it.deleteRecursively() }

    println("Press enter when you've edited CHANGELOG.md")

    readln()

    for (jar in jarWithSignature.keys) {
        val loader = getLoaderFromJarName(jar.name)
        val mcVersion = getVersionFromJarName(jar.name)

        outlet.mcVersionRange = getVersionRangeFabricFromJarName(jar.name)

        uploadModrinthVersion(
            projectId = "gblzqx92",
            token = (System.getenv("MODRINTH_TOKEN") ?: project.findProperty("MODRINTH_TOKEN")) as String,
            primaryFile = jar,
            additionalFiles = mapOf(jarWithSignature[jar]!! to "signature"),
            versionNumber = "$mod_version-${loader}-${mcVersion}",
            versionName = "JUST SHOW ME MY MAP! version $mod_version",
            gameVersions = outlet.mcVersions().map { fixPreReleaseText(it) }.toList(), // TODO: CHANGE THIS
            loaders = listOf(loader), // TODO: CHANGE THIS ALSO FOR SOME MODLOADERS
            changelog = "Supported versions: ${getVersionRangeFromJarName(jar.name)}\n\n" + rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
        )
    }
}

fun addToZip(file: File, relativeFile: File, zos: ZipOutputStream) {
    val relativePath = file.relativeTo(relativeFile).path
    zos.putNextEntry(ZipEntry(relativePath))
    file.inputStream().use { it.copyTo(zos) }
    zos.closeEntry()
}

fun getLoaderFromJarName(name: String): String {
    return name.split("-")[2]
}

fun getVersionFromJarName(name: String): String {
    return name.replace("${project.name}-$mod_version-${getLoaderFromJarName(name)}-", "").replace(".jar", "")
}

fun getProjectFromJarName(name: String): Project {
    return project(":loaders:${getLoaderFromJarName(name)}:${getVersionFromJarName(name)}")
}

fun getVersionRangeFromJarName(name: String): String {
    return getProjectFromJarName(name).property("versionRange")!! as String
}

fun getVersionRangeFabricFromJarName(name: String): String {
    return if (getLoaderFromJarName(name) == "fabric" || getLoaderFromJarName(name) == "ornithe") {
        getProjectFromJarName(name).property("supportedMcVersions")!! as String
    } else if (getLoaderFromJarName(name) == "liteloader") {
        getProjectFromJarName(name).property("versionRange")!! as String
    } else {
        getProjectFromJarName(name).property("versionRangeFabric")!! as String
    }
}

fun fixPreReleaseText(version: String): String = version.replace(Regex("""(\d+\.\d+(?:\.\d+)?)\s+Pre-Release\s+(\d+)""", RegexOption.IGNORE_CASE)) {
    val (base, num) = it.destructured
    "$base-pre$num"
}

tasks.register("release") {
    description = "Does all the things needed for a release. This is an interactive task."
    group = "jsmmm"

    // I'm under the assumption every project has at least a dot in it
    val subsubprojects = subprojects
        .filter { it.path.startsWith(":loaders:") && it.path.contains(".")}

    subsubprojects.forEach {
        dependsOn(it.tasks.named("build"))
    }

    doLast {
        val dir = project.layout.buildDirectory.dir("release").get().asFile
        dir.mkdirs()

        val jars = subsubprojects
            .flatMap { sub ->
                sub.layout.buildDirectory.dir("libs").get().asFile
                    .listFiles { f ->
                        f.name.startsWith("jsmmm-") && f.name.endsWith(".jar")
                    }
                    ?.toList() ?: emptyList()
            }
            .sortedBy { it.name }

        if (jars.isEmpty()) {
            error("Somehow there aren't any jars built.")
        }

        println("Found ${jars.size} jars.")

        releaseGithub(jars)
    }
}