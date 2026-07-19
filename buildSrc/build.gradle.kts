// buildSrc/build.gradle.kts
plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.fabricmc:fabric-loom:1.17-SNAPSHOT")
    implementation("net.fabricmc.fabric-loom-remap:net.fabricmc.fabric-loom-remap.gradle.plugin:1.17.16")
}
