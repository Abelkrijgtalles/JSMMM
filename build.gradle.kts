import net.fabricmc.loom.task.FabricModJsonV1Task

plugins {
	id("net.fabricmc.fabric-loom")
	id("maven-publish")
}

val minecraft_version: String by project
val loader_version: String by project
val fabric_api_version: String by project

val mod_version: String by project
val maven_group: String by project

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register(rootProject.name) {
			sourceSet(sourceSets["client"])
		}
	}

}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraft_version}")
	
	implementation("net.fabricmc:fabric-loader:${loader_version}")
	
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(25)
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.named<Jar>("jar") {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

tasks.register("generateModJson", FabricModJsonV1Task::class) {
	description = "Generate fabric.mod.json"
	outputFile = file("$projectDir/src/client/resources/fabric.mod.json")

	json {
		modId = rootProject.name
		version = mod_version
		name = "JUST SHOW ME MY MAP!"
		description = "Shows your map while rowing a boat. That's it."
		contactInformation.put("homepage", "https://github.com/Abelkrijgtalles/JSMMM")
		contactInformation.put("sources", "https://github.com/Abelkrijgtalles/JSMMM")
		contactInformation.put("issues", "https://github.com/Abelkrijgtalles/JSMMM/issues")
		contactInformation.put("modrinth", "https://modrinth.com/project/jsmmm")
		author("Abelpro678") {
			contactInformation.put("homepage", "https://github.com/Abelkrijgtalles")
			contactInformation.put("modrinth", "https://modrinth.com/user/Abelpro678")
		}
		// Add contributors once I have them
		licenses.add("GPL-3.0-or-later")
		icon("assets/jsmmm/icon.png")
		environment = "client"
		mixin("jsmmm.client.mixins.json") {
			environment = "client"
		}
		depends("fabricloader", ">=$loader_version")
		depends("minecraft", ">=$minecraft_version")
		depends("java", ">=25")
	}
}

// Make sure generateModJson
tasks.withType<ProcessResources>().configureEach {
	dependsOn(tasks.named<FabricModJsonV1Task>("generateModJson"))
}

tasks.withType<Jar>().configureEach {
	dependsOn(tasks.named<FabricModJsonV1Task>("generateModJson"))
}