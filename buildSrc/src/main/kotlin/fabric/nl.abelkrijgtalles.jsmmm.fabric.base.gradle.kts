import net.fabricmc.loom.task.FabricModJsonV1Task
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("nl.abelkrijgtalles.jsmmm.base")
    id("nl.abelkrijgtalles.jsmmm.base.with-mixin-compatibility-level")
}

val javaVersion: String by project
val supportedMcVersions: String by project
val mcVersion: String by project

sourceSets["main"].resources.srcDir(
    layout.buildDirectory.dir("generated/resources")
)

tasks.register("generateModJson", FabricModJsonV1Task::class)  {
    description = "Generates fabric.mod.json"
    outputFile.set(layout.buildDirectory.file("generated/resources/fabric.mod.json"))

    json {
        modId = rootProject.name
        version = project.version.toString()
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
        licenses.add("LGPL-3.0-or-later")
        icon("assets/jsmmm/icon.png")
        environment = "client"
        mixin("jsmmm.client.mixins.json") {
            environment = "client"
        }
        depends("fabricloader", ">=${libs.`fabric-loader`.get().version}")
        depends("java", ">=$javaVersion")
        depends("minecraft", supportedMcVersions)
    }
}

dependencies.add("minecraft","com.mojang:minecraft:$mcVersion")

tasks.withType<ProcessResources>().configureEach {
    dependsOn("generateModJson")
}
tasks.withType<Jar>().configureEach {
    dependsOn("generateModJson")
}
tasks.withType<RemapJarTask>().configureEach {
    dependsOn("generateModJson")

    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("${parent?.name}-${project.name}")
}