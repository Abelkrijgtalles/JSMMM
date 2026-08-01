plugins {
    java
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.renamer")
}

configureJSMMMForgeTarget()

val javaVersion: String by project
val forgeVersion: String by project

java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

renamer.enableMixinRefmaps {
    config("jsmmm.client.mixins.json")
}

renamer.classes(tasks.named<Jar>("jar")) {
    archiveClassifier.set("${parent?.name}-${project.name}")
    mappings(renamer.mixin.generatedMappings)
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
    // I'm using Fabric's fork, because this also remaps shadowed methods
    annotationProcessor("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
}

renamer.mappings(minecraft.dependency.toSrg)

// I don't really know what will happen if I don't expand the value in the other projects, so I'll just do it this way
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
