pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
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