plugins {
    id("nl.abelkrijgtalles.jsmmm.forge.obfuscated")
}

renamer.classes(tasks.named<Jar>("jar")) {
    archiveClassifier.set("${parent?.name}-${project.name}")
}

tasks.processResources {
    exclude("jsmmm.client.mixins.json")
}