// buildSrc/src/main/kotlin/JsmmmCommon.kt
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.FabricModJsonV1Task
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.getByType

fun Project.configureJsmmmCommon() {
    val mcVersion: String by project
    val loaderVersion: String by project
    val javaVersion: String by project
    val symbol: String by project
    val modVersion = rootProject.property("mod_version") as String   

    // eigen, niet-gedeelde map per subproject
    val generatedResources = layout.buildDirectory.dir("generated/resources")
    val sourceSets = extensions.getByType<SourceSetContainer>()   // <-- expliciet ophalen
    sourceSets.getByName("client").resources.srcDir(generatedResources)

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(javaVersion.toInt())
        options.compilerArgs.addAll(listOf(
            "-Xplugin:Manifold",
            "-A$symbol"
        ))
    }

    dependencies.add("annotationProcessor", "systems.manifold:manifold-preprocessor:2024.1.32")

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) { rename { "${it}_${project.name}" } }
    }

    tasks.register("generateModJson", FabricModJsonV1Task::class) {
        outputFile = generatedResources.get().file("fabric.mod.json").asFile
        json {
            modId = "jsmmm"
            version = modVersion
            name = "JUST SHOW ME MY MAP!"
            description = "Shows your map while rowing a boat. That's it."
            contactInformation.put("homepage", "https://github.com/Abelkrijgtalles/JSMMM")
            depends("fabricloader", ">=$loaderVersion")
            depends("java", ">=$javaVersion")
            depends("minecraft", "=$mcVersion")
        }
    }

    tasks.withType<ProcessResources>().configureEach { dependsOn("generateModJson") }
    tasks.withType<Jar>().configureEach { dependsOn("generateModJson") }
    tasks.withType<RemapJarTask>().configureEach { dependsOn("generateModJson") }
}
