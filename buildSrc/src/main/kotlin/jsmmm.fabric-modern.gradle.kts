// buildSrc/src/main/kotlin/jsmmm.fabric-modern.gradle.kts
plugins { id("net.fabricmc.fabric-loom") }

val mcVersion: String by project
val loaderVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

configureJsmmmFabricTarget("fabric")

dependencies {
    implementation("net.fabricmc:fabric-loader:$loaderVersion")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set(mcVersion)
}