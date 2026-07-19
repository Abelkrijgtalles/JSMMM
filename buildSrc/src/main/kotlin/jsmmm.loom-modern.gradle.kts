plugins {
    id("net.fabricmc.fabric-loom")
}

val mcVersion: String by project
val loaderVersion: String by project

loom {
    splitEnvironmentSourceSets()
    mods { register("jsmmm") { sourceSet(sourceSets["client"]) } }
}

sourceSets["client"].java.srcDirs(rootProject.projectDir.resolve("src/client/java"))
sourceSets["client"].resources.srcDirs(rootProject.projectDir.resolve("src/client/resources"))

configureJsmmmCommon()

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    // no mappings dependency — the jar already ships with real names
    implementation("net.fabricmc:fabric-loader:$loaderVersion") // plain, no remap needed
}

tasks.named<Jar>("jar") {   // output task is jar here, remapJar doesn't exist
    archiveClassifier.set(mcVersion)
}
