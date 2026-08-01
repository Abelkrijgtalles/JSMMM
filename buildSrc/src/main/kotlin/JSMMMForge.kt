import org.apache.tools.ant.filters.LineContains
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.kotlin.dsl.*

fun Project.configureJSMMMForgeTarget() {
    val majorForgeVersion: String by project
    val javaVersion: String by project
    val supportedForgeVersions: String by project
    val supportedMinecraftVersions: String by project
    val symbol: String by project

    group = rootProject.property("maven_group") as String
    version = rootProject.property("mod_version") as String

    tasks.withType<ProcessResources>().configureEach {
        dependsOn(tasks["compileJava"])
        val replaceProperties = mapOf(
            "major_forge_version" to majorForgeVersion,
            "mod_version" to version,
            "java_version" to javaVersion,
            "supported_forge_versions" to supportedForgeVersions,
            "supported_minecraft_versions" to supportedMinecraftVersions,
        )

        inputs.properties(replaceProperties)
        filesMatching("META-INF/mods.toml") {
            expand(replaceProperties)
        }
        into("build/generated/sources/modMetadata")

        filesMatching("jsmmm.client.mixins.json") {
            filter(
                mapOf(
                    "negate" to true,
                    "contains" to listOf("\"compatibilityLevel\"")
                ),
                LineContains::class.java
            )
        }
    }

    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets["main"].java.srcDirs(
        rootProject.file("common/src/client/java"),
        rootProject.file("loaders/forge/src/main/java"),
    )

    sourceSets["main"].resources.srcDirs(
        rootProject.file("common/src/client/resources"),
        rootProject.file("loaders/forge/src/main/resources"),
        "src/generated/resources",
        "build/generated/sources",
    )

    repositories {
        mavenCentral()
    }

    tasks.named<JavaCompile>("compileJava") {
        options.release.set(javaVersion.toInt())
        options.compilerArgs.add("-Xplugin:Manifold")
        options.compilerArgs.addAll(symbol.split(" ").map { "-A$it" })
    }

    dependencies {
        dependencies.add("annotationProcessor", "systems.manifold:manifold-preprocessor:2026.1.8")
    }

    tasks.named<Jar>("jar") {
        archiveBaseName.set(rootProject.name)
    }
}