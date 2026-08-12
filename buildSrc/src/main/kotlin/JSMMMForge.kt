import org.apache.tools.ant.filters.LineContains
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.kotlin.dsl.*

enum class ForgeModFile {
    MODS_TOML,
    MCMOD,
    LITEMOD
}

fun Project.configureJSMMMForgeTarget(modFile: ForgeModFile) {
    val forgeVersion: String by project
    val majorForgeVersion: String by project
    val javaVersion: String by project
    val supportedForgeVersions: String by project
    val supportedMinecraftVersions: String by project
    val symbol: String by project
    val namespace: String by project

    group = rootProject.property("maven_group") as String
    version = rootProject.property("mod_version") as String

    tasks.withType<ProcessResources>().configureEach {
        dependsOn(tasks["compileJava"])
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        when (modFile) {
            ForgeModFile.MODS_TOML -> {
                exclude("mcmod.info")
            }
            ForgeModFile.MCMOD -> {
                exclude("META-INF/mods.toml")
            }
            ForgeModFile.LITEMOD -> {
                exclude("META-INF/mods.toml")
                exclude("mcmod.info")
            }
        }

        val replaceProperties: Map<String, Any> = when (modFile) {
            ForgeModFile.MODS_TOML -> {
                mapOf(
                    "major_forge_version" to majorForgeVersion,
                    "mod_version" to version,
                    "java_version" to javaVersion,
                    "supported_forge_versions" to supportedForgeVersions,
                    "supported_minecraft_versions" to supportedMinecraftVersions,
                )
            }
            ForgeModFile.LITEMOD -> {
                mapOf(
                    "minecraft_version" to forgeVersion.split("-")[0],
                    "mod_version" to version,
                )
            }
            else -> {
                mapOf(
                    "mod_version" to version,
                )
            }
        }

        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/mods.toml", "mcmod.info", "litemod.json")) {
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

    if (namespace != "official") {
        val remapCommon = registerSourceRemapTask(
            inputDir = rootProject.file("common/src/client/java"),
            targetNamespace = namespace
        )
        sourceSets["main"].java.srcDirs(remapCommon)
    } else {
        sourceSets["main"].java.srcDirs(rootProject.file("common/src/client/java"))
    }

    sourceSets["main"].java.srcDirs(
        parent?.file("src/main/java"),
        project.file("src/main/java")
    )

    sourceSets["main"].resources.srcDirs(
        rootProject.file("common/src/client/resources"),
        rootProject.file("loaders/forge/src/main/resources"),
        parent?.file("src/main/resources"),
        "src/generated/resources",
        "build/generated/sources",
        project.file("src/main/resources")
    )

    repositories {
        mavenCentral()
    }

    tasks.named<JavaCompile>("compileJava") {
        val javaVersionInt = javaVersion.toInt()
        if (javaVersionInt >= 9) {
            options.release.set(javaVersionInt)
        } else {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
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