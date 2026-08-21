val majorForgeVersion: String by project
val javaVersion: String by project
val supportedForgeVersions: String by project
val supportedMinecraftVersions: String by project

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "major_forge_version" to majorForgeVersion,
        "mod_version" to version,
        "java_version" to javaVersion,
        "supported_forge_versions" to supportedForgeVersions,
        "supported_minecraft_versions" to supportedMinecraftVersions,
    )

    filesMatching("META-INF/mods.toml") {
        expand(replaceProperties)
    }
    into("build/generated/sources/modMetadata")

    exclude("mcmod.info")
}