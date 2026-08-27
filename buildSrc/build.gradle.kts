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
    implementation(libs.fabric.loom)
    implementation(libs.fabric.loom.remapped)
    implementation(libs.neoforge.gradle)
    implementation(libs.forge.gradle)
    implementation(libs.forge.renamer)
    implementation(libs.ploceus)
}
