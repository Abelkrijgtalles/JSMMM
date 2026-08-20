plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.neoforged.net/releases") { name = "NeoForge" }
    maven("https://maven.ornithemc.net/releases") { name = "Ornithe Releases" }
    maven("https://maven.ornithemc.net/snapshots") { name = "Ornithe Snapshots" }
    maven("https://maven.wagyourtail.xyz/snapshots/") { name = "WagYourMaven" }
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.fabric.loom)
    implementation(libs.fabric.loom.remapped)
    implementation(libs.neoforge.gradle)
    implementation("net.minecraftforge.gradle:net.minecraftforge.gradle.gradle.plugin:7.0.31")
    implementation("net.minecraftforge.renamer:net.minecraftforge.renamer.gradle.plugin:1.1.7")
    implementation(libs.ploceus)
    implementation("net.fabricmc:mapping-io:0.9.1")
}
