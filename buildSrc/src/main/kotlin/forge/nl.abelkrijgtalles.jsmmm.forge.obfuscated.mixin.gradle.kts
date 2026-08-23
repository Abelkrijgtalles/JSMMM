plugins {
    id("nl.abelkrijgtalles.jsmmm.forge.obfuscated")
}

renamer.enableMixinRefmaps {
    config("jsmmm.client.mixins.json")
}

renamer.classes(tasks.named<Jar>("jar")) {
    archiveClassifier.set("${parent?.name}-${project.name}")
    mappings(renamer.mixin.generatedMappings)
}

tasks.processResources {
    filesMatching("jsmmm.client.mixins.json") {
        filter { line ->
            line.replace(
                "\"package\": \"nl.abelkrijgtalles.jsmmm.mixin\",",
                "\"package\": \"nl.abelkrijgtalles.jsmmm.mixin\",\n\t\"refmap\": \"main.refmap.json\","
            )
        }
    }
}

repositories {
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

dependencies {
    annotationProcessor(libs.`mixin-ap`)
}