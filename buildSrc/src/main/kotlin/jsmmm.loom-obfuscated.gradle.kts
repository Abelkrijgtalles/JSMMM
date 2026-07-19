import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("net.fabricmc.fabric-loom-remap")
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
    mappings(loom.officialMojangMappings())                        // needed only when obfuscated
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion") // "mod" prefix = get remapped
}

tasks.named<RemapJarTask>("remapJar") {   // output task is remapJar here
    archiveClassifier.set(mcVersion)
}
