import groovy.json.JsonSlurper
import nl.abelkrijgtalles.jsmmm.mappingsgenerator.MappingsGenerator
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import java.io.File
import java.net.URI
import java.security.MessageDigest

private const val VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"

fun Project.resolveMcClientJar(mcVersion: String): File {
    val cacheDir = rootProject.layout.buildDirectory.dir("mc-jars").get().asFile.apply { mkdirs() }
    val jarFile = File(cacheDir, "client-$mcVersion.jar")

    val manifest = JsonSlurper().parse(URI(VERSION_MANIFEST_URL).toURL()) as Map<*, *>
    @Suppress("UNCHECKED_CAST")
    val versions = manifest["versions"] as List<Map<String, Any>>
    val versionEntry = versions.firstOrNull { it["id"] == mcVersion }
        ?: throw IllegalStateException("Minecraft version '$mcVersion' not found in version manifest")

    val versionMeta = JsonSlurper().parse(URI(versionEntry["url"] as String).toURL()) as Map<*, *>
    @Suppress("UNCHECKED_CAST")
    val client = (versionMeta["downloads"] as Map<String, Any>)["client"] as Map<String, Any>
    val downloadUrl = client["url"] as String
    val expectedSha1 = client["sha1"] as String

    fun File.sha1(): String {
        val digest = MessageDigest.getInstance("SHA-1")
        inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    if (jarFile.exists() && jarFile.sha1() == expectedSha1) {
        return jarFile
    }

    logger.lifecycle("Downloading Minecraft $mcVersion client jar...")
    URI(downloadUrl).toURL().openStream().use { input ->
        jarFile.outputStream().use { output -> input.copyTo(output) }
    }

    check(jarFile.sha1() == expectedSha1) {
        "SHA1 mismatch for downloaded Minecraft client jar (expected $expectedSha1)"
    }

    return jarFile
}

private fun remapTaskNameFor(targetNamespace: String): String {
    val pascalCase = targetNamespace
        .split(Regex("[^A-Za-z0-9]+"))
        .filter { it.isNotEmpty() }
        .joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }
    return "remapCommonSourcesTo$pascalCase"
}

fun Project.getOrRegisterSourceRemapTask(
    inputDir: File,
    targetNamespace: String,
): TaskProvider<JavaExec> {
    val taskName = remapTaskNameFor(targetNamespace)

    if (taskName in rootProject.tasks.names) {
        return rootProject.tasks.named(taskName, JavaExec::class.java)
    }

    return rootProject.tasks.register<JavaExec>(taskName) {
        val mapsTxt: File = rootProject.file("maps.txt")
        val mappingsFile: File = rootProject.layout.buildDirectory.file("maps.tiny").get().asFile
        val outputDir: File = rootProject.layout.buildDirectory.dir("generated/$targetNamespace").get().asFile

        rootProject.repositories.maven("https://maven.wagyourtail.xyz/snapshots/")
        rootProject.repositories.mavenCentral()
        rootProject.repositories.maven("https://repo.spongepowered.org/maven/")
        val toolCp = rootProject.configurations.detachedConfiguration(
            // Doesn't want to work if I add it to libs.versions.toml, so I'll do it like this
            rootProject.dependencies.create("xyz.wagyourtail.unimined:source-remap:1.0.5-SNAPSHOT:all")
        )
        val mixinCp = rootProject.configurations.detachedConfiguration(
            libs.mixin.get()
        )

        classpath = toolCp
        mainClass.set("com.replaymod.gradle.remap.MainKt")

        doFirst {
            if (!mappingsFile.exists() || mapsTxt.lastModified() > mappingsFile.lastModified()) {
                MappingsGenerator.generateMappings(mapsTxt.toPath(), mappingsFile.toPath())
            }
            val mcJar = rootProject.resolveMcClientJar("26.2")
            val fullClasspath = (listOf(mcJar) + mixinCp.files)
                .distinct()
                .joinToString(File.pathSeparator) { it.absolutePath }
            args = listOf(
                "-cp", fullClasspath,
                "-m", mappingsFile.absolutePath,
                "-r", inputDir.absolutePath, outputDir.absolutePath,
                "-t", targetNamespace,
            )
        }
        inputs.dir(inputDir)
        inputs.file(mapsTxt)
        outputs.dir(outputDir)
    }
}