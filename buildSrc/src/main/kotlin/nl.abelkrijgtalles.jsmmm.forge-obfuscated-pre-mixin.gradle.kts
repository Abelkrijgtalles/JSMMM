plugins {
    java
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.renamer")
}

configureJSMMMForgeTarget(true)

val javaVersion: String by project
val forgeVersion: String by project
//val at: String? = providers.gradleProperty("at").orNull

java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

renamer.classes(tasks.named<Jar>("jar")) {
    archiveClassifier.set("${parent?.name}-${project.name}")
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

minecraft {
    useDefaultAccessTransformer()
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
}

renamer.mappings(minecraft.dependency.toSrg)

tasks.processResources {
    exclude("jsmmm.client.mixins.json")
}