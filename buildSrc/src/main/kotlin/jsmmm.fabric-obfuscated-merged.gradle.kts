import net.fabricmc.loom.task.RemapJarTask

plugins { id("net.fabricmc.fabric-loom-remap") }

val mcVersion: String by project

loom {
    mods { register("jsmmm") { sourceSet(sourceSets["main"]) } }
}

configureJsmmmFabricTarget("0.19.3", false)

dependencies {
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set(project.name)
}