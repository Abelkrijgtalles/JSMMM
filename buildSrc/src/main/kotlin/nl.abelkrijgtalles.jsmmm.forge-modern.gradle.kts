plugins {
    java
    id("net.minecraftforge.gradle")
}

configureJSMMMForgeTarget()

val javaVersion: String by project
val forgeVersion: String by project

java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    dependencies.add("implementation",minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["MixinConfigs"] = "jsmmm.client.mixins.json"
    }
    archiveClassifier.set("${parent?.name}-${project.name}")
}