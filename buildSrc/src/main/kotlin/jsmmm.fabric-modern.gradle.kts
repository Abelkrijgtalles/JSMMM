// buildSrc/src/main/kotlin/jsmmm.fabric-modern.gradle.kts
plugins { id("net.fabricmc.fabric-loom") }

val mcVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

configureJsmmmFabricTarget("0.19.3")

dependencies {
    implementation("net.fabricmc:fabric-loader:0.19.3")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set(mcVersion)
}