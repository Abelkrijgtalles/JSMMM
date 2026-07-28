plugins {
    id("net.neoforged.gradle.userdev")
}

val neoForgeVersion: String by project
val javaVersion: String by project
val symbol: String by project
val supportedNeoForgeVersions: String by project
val neoForgeModsToml: String by project

group = rootProject.property("maven_group") as String
version = rootProject.property("mod_version") as String

tasks.withType<ProcessResources>().configureEach {
    dependsOn(tasks.compileJava)
    val replaceProperties = mapOf(
        "mod_version" to version,
        "java_version" to javaVersion,
        "supported_neo_versions" to supportedNeoForgeVersions,
    )

    inputs.properties(replaceProperties)
    from(rootProject.file("loaders/neoforge/src/main/templates")) {
        expand(replaceProperties)
        if (!neoForgeModsToml.toBoolean()) {
            rename("neoforge\\.mods\\.toml", "mods.toml")
        }
    }
    into("build/generated/sources/modMetadata")

    filesMatching("jsmmm.client.mixins.json") {
        expand("mixinCompatibilityLevel" to mixinCompatibilityLevelFor(javaVersion))
    }
}

sourceSets["main"].java.srcDirs(
    rootProject.file("common/src/client/java"),
    rootProject.file("loaders/neoforge/src/main/java"),
)

sourceSets["main"].resources.srcDirs(
    rootProject.file("common/src/client/resources"),
    rootProject.file("loaders/neoforge/src/client/resources"),
    "src/generated/resources",
    "build/generated/sources",
)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

tasks.named<JavaCompile>("compileJava") {
    options.release.set(javaVersion.toInt())
    options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "-A$symbol"))
}

dependencies {
    annotationProcessor("systems.manifold:manifold-preprocessor:2026.1.8")
    implementation("net.neoforged:neoforge:$neoForgeVersion")
}

tasks.named<Jar>("jar") {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("${parent?.name}-${project.name}")
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("jsmmm.client.mixins.json") {
        expand("mixinCompatibilityLevel" to mixinCompatibilityLevelFor(javaVersion))
    }
}
