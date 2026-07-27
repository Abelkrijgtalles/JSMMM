import net.fabricmc.loom.task.RemapJarTask

plugins { id("net.fabricmc.fabric-loom-remap") }

val mcVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

configureJSMMMFabricTarget("0.19.3", true)

dependencies {
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("${parent?.name}-${project.name}")
}