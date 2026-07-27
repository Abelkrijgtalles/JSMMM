import net.fabricmc.loom.task.RemapJarTask

plugins { id("net.fabricmc.fabric-loom-remap") }

val mcVersion: String by project
val yarnVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

configureJsmmmFabricTarget("0.19.3")

dependencies {
    mappings("net.fabricmc.yarn:$yarnVersion")
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier.set(mcVersion)
}