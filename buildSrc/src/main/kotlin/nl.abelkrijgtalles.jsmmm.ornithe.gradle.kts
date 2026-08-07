import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.named

plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("ploceus")
}

val featherBuild: String by project

configureJSMMMFabricTarget("0.19.3", false)

ploceus {
    setIntermediaryGeneration(2)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
    mappings(ploceus.featherMappings(featherBuild))
}

tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("${parent?.name}-${project.name}")
}

tasks.processResources {
    filesMatching("jsmmm.client.mixins.json") {
        filter { line ->
            line.replace(
                "\"LocalPlayerMixin\"",
                "\"LocalClientPlayerEntityMixin\""
            )
        }
    }
}