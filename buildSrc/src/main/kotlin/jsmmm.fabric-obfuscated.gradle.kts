// buildSrc/src/main/kotlin/jsmmm.fabric-obfuscated.gradle.kts
import net.fabricmc.loom.task.RemapJarTask

plugins { id("net.fabricmc.fabric-loom-remap") }

val mcVersion: String by project
val loaderVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

configureJsmmmFabricTarget("fabric")

dependencies {
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier.set(mcVersion)
}