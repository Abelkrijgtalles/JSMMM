import net.fabricmc.loom.task.FabricModJsonV1Task
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.kotlin.dsl.*

fun mixinCompatibilityLevelFor(javaVersion: String): String {
    val level = javaVersion.toInt()
    return "JAVA_$level"
}

fun Project.configureJSMMMFabricTarget(loaderVersion: String, split: Boolean) {
    val mcVersion: String by project
    val javaVersion: String by project
    val symbol: String by project
    val supportedMcVersions: String by project

    val generatedResources = layout.buildDirectory.dir("generated/resources")
    val sourceSets = extensions.getByType<SourceSetContainer>()
    val targetSourceSet = if (split) "client" else "main"

    sourceSets[targetSourceSet].java.srcDirs(
        rootProject.file("common/src/client/java"),
        rootProject.file("loaders/fabric/src/client/java"),
    )
    sourceSets[targetSourceSet].resources.srcDirs(
        rootProject.file("common/src/client/resources"),
        rootProject.file("loaders/fabric/src/client/resources"),
        generatedResources,
    )

    group = rootProject.property("maven_group") as String
    version = rootProject.property("mod_version") as String
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(javaVersion.toInt())
        options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "-A$symbol"))
    }

    dependencies.add("annotationProcessor", "systems.manifold:manifold-preprocessor:2026.1.8")
    dependencies.add("minecraft", "com.mojang:minecraft:$mcVersion")   // <- eager now, fixes the bug

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) { rename { "${it}_JSMMM" } }
    }

    // Also edit this in neoforge.mods.toml
    val modVersion = rootProject.property("mod_version") as String
    tasks.register("generateModJson", FabricModJsonV1Task::class) {
        outputFile = generatedResources.get().file("fabric.mod.json").asFile
        json {
            modId = rootProject.name
            version = modVersion
            name = "JUST SHOW ME MY MAP!"
            description = "Shows your map while rowing a boat. That's it."
            contactInformation.put("homepage", "https://github.com/Abelkrijgtalles/JSMMM")
            contactInformation.put("sources", "https://github.com/Abelkrijgtalles/JSMMM")
            contactInformation.put("issues", "https://github.com/Abelkrijgtalles/JSMMM/issues")
            contactInformation.put("modrinth", "https://modrinth.com/project/jsmmm")
            author("Abelpro678") {
                contactInformation.put("homepage", "https://github.com/Abelkrijgtalles")
                contactInformation.put("modrinth", "https://modrinth.com/user/Abelpro678")
            }
            // Add contributors once I have them
            licenses.add("GPL-3.0-or-later")
            icon("assets/jsmmm/icon.png")
            environment = "client"
            mixin("jsmmm.client.mixins.json") {
                environment = "client"
            }
            depends("fabricloader", ">=$loaderVersion")
            depends("java", ">=$javaVersion")
            depends("minecraft", supportedMcVersions)
        }
    }

    tasks.withType<ProcessResources>().configureEach { dependsOn("generateModJson") }
    tasks.withType<Jar>().configureEach { dependsOn("generateModJson") }
    tasks.withType<RemapJarTask>().configureEach { dependsOn("generateModJson") }

    val processResourcesTaskName =
        if (targetSourceSet == "main") "processResources"
        else "process${targetSourceSet.replaceFirstChar(Char::uppercase)}Resources"

    tasks.named<ProcessResources>(processResourcesTaskName) {
        filesMatching("jsmmm.client.mixins.json") {
            expand("mixinCompatibilityLevel" to mixinCompatibilityLevelFor(javaVersion))
        }
    }
}