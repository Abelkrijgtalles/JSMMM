plugins {
    id("nl.abelkrijgtalles.jsmmm.neoforge.base")
}

val javaVersion: String by project
val supportedNeoForgeVersions: String by project

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "mod_version" to version,
        "java_version" to javaVersion,
        "supported_neo_versions" to supportedNeoForgeVersions,
    )
    from(parent!!.file("src/main/templates")) {
        expand(replaceProperties)
    }
    into("build/generated/sources/modMetadata")
}