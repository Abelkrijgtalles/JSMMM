import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

plugins {
    id("signing")
}

signing {
    useGpgCmd()
}

val mod_version: String by project

fun Project.releaseGithub(jars: List<File>) {
    val githubReleaseDir = project.layout.buildDirectory.dir("release").get().dir("github")
    githubReleaseDir.asFile.mkdirs()
    println("Moving all jars to ${githubReleaseDir.asFile.absolutePath}.")

    val filesToZip = mutableListOf<File>()

    for (jar in jars) {
        val copiedJar = jar.copyTo(githubReleaseDir.asFile.resolve(jar.name), true)
        val signature = signing.sign(copiedJar).signatureFiles.first()

        val loaderDir = githubReleaseDir.dir(jar.name.split("-")[2]).asFile
        loaderDir.mkdirs()
        copiedJar.copyTo(loaderDir.resolve(jar.name), true)
        signature.copyTo(loaderDir.resolve(signature.name), true)

        filesToZip += copiedJar
        filesToZip += signature
    }

    val zipFile = githubReleaseDir.asFile.resolve("$name-$mod_version.zip")
    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
        zos.setLevel(Deflater.BEST_COMPRESSION)
        for (file in filesToZip) {
            val relativePath = file.relativeTo(githubReleaseDir.asFile).path
            zos.putNextEntry(ZipEntry(relativePath))
            file.inputStream().use { it.copyTo(zos) }
            zos.closeEntry()
        }
    }
    // let's sign the zipfiles 'cuz why not
    signing.sign(zipFile)

    for (loader in githubReleaseDir.asFile.listFiles()!!.filter { it.isDirectory }) {
        val loaderZipFile = githubReleaseDir.asFile.resolve("$name-$mod_version-${loader.name}.zip")
        ZipOutputStream(BufferedOutputStream(FileOutputStream(loaderZipFile))).use { zos ->
            zos.setLevel(Deflater.BEST_COMPRESSION)
            for (file in loader.listFiles()!!.sortedBy { it.name }) {
                val relativePath = file.relativeTo(loader).path
                zos.putNextEntry(ZipEntry(relativePath))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
        signing.sign(loaderZipFile)
    }
}

tasks.register("release") {
    description = "Does all the things needed for a release. This is an interactive task"
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