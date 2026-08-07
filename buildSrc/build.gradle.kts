plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.neoforged.net/releases") { name = "NeoForge" }
    maven("https://maven.ornithemc.net/releases") { name = "Ornithe Releases" }
    maven("https://maven.ornithemc.net/snapshots") { name = "Ornithe Snapshots" }
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.fabricmc:fabric-loom:1.17-SNAPSHOT")
    implementation("net.fabricmc.fabric-loom-remap:net.fabricmc.fabric-loom-remap.gradle.plugin:1.17-SNAPSHOT")
    implementation("net.neoforged.gradle.userdev:net.neoforged.gradle.userdev.gradle.plugin:7.1.38")
    implementation("net.minecraftforge.gradle:net.minecraftforge.gradle.gradle.plugin:7.0.31")
    implementation("net.minecraftforge.renamer:net.minecraftforge.renamer.gradle.plugin:1.1.7")
    implementation("ploceus:ploceus.gradle.plugin:1.17-SNAPSHOT")
}
