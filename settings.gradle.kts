pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		mavenCentral()
		gradlePluginPortal()
	}
	val loom_version: String by settings
	plugins {
		id("net.fabricmc.fabric-loom") version loom_version
	}
}

// Should match your modid
rootProject.name = "jsmmm"