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

java.util.Properties().apply { load(file("versions.properties").inputStream()) }
    .forEach { (id, _) ->
        include(":versions:$id")
        project(":versions:$id").projectDir = file("versions/$id")
    }
