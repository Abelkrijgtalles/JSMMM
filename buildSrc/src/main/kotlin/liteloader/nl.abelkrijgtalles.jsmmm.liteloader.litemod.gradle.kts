val forgeVersion: String by project

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraft_version" to forgeVersion.split("-")[0],
        "mod_version" to version,
    )

    inputs.properties(replaceProperties)
    filesMatching("litemod.json") {
        expand(replaceProperties)
    }
    into("build/generated/sources/modMetadata")

    exclude("assets/jsmmm/icon.png")
}