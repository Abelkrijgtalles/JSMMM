pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
        id("net.fabricmc.fabric-loom-remap") version "1.17.16"
    }
}

rootProject.name = "jsmmm"

file("loaders").listFiles { f -> f.isDirectory }?.forEach { loaderDir ->
    val loaderName = loaderDir.name
    loaderDir.listFiles { f ->
        f.isDirectory && f.name != "src" && f.resolve("build.gradle.kts").exists()
    }?.forEach { versionDir ->
        val path = ":loaders:$loaderName:${versionDir.name}"
        include(path)
        project(path).projectDir = versionDir
    }
}

include("common")