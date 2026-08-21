tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "mod_version" to version,
    )

    filesMatching("mcmod.info") {
        expand(replaceProperties)
    }
    into("build/generated/sources/modMetadata")

    exclude("META-INF/mods.toml")
}