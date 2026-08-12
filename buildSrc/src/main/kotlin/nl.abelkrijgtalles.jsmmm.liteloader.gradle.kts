plugins {
    java
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.renamer")
}

configureJSMMMForgeTarget(ForgeModFile.LITEMOD)

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
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
    compileOnly("org.spongepowered:mixin:0.8.5")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

renamer.mappings(minecraft.dependency.toObf)

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