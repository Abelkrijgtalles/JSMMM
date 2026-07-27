plugins { id("net.fabricmc.fabric-loom") }

val mcVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

configureJsmmmFabricTarget("0.19.3", true)

dependencies {
    implementation("net.fabricmc:fabric-loader:0.19.3")
}

tasks.named<Jar>("jar") {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set(project.name)
}