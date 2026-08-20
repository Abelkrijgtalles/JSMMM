plugins {
    id("net.neoforged.gradle.userdev")
    id("nl.abelkrijgtalles.jsmmm.base")
    id("nl.abelkrijgtalles.jsmmm.base.with-mixin-compatibility-level")
}

val javaVersion: String by project
val supportedNeoForgeVersions: String by project
val neoForgeVersion: String by project

tasks.withType<ProcessResources>().configureEach {
    dependsOn(tasks.compileJava)
    val replaceProperties = mapOf(
        "mod_version" to version,
        "java_version" to javaVersion,
        "supported_neo_versions" to supportedNeoForgeVersions,
    )
    inputs.properties(replaceProperties)
    from(parent!!.file("src/main/templates")) {
        expand(replaceProperties)
    }
    into("build/generated/sources/modMetadata")
}

sourceSets["main"].resources.srcDirs(
    "build/generated/sources",
)

dependencies {
    implementation("net.neoforged:neoforge:$neoForgeVersion")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))