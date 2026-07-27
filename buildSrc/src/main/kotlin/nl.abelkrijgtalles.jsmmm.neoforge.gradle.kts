plugins {
    id("net.neoforged.moddev")
}

val neoForgeVersion: String by project
val javaVersion: String by project
val symbol: String by project

group = rootProject.property("maven_group") as String
version = rootProject.property("mod_version") as String

val generatedResources = layout.buildDirectory.dir("generated/resources")

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    var replaceProperties = mapOf(
        "version" to version,
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("loaders/neoforge/src/main/templates"))
    into("build/generated/sources/modMetadata")
}

sourceSets["main"].java.srcDirs(
    rootProject.file("common/src/client/java"),
    rootProject.file("loaders/neoforge/src/client/java"),
)

sourceSets["main"].resources.srcDirs(
    rootProject.file("common/src/client/resources"),
    rootProject.file("loaders/neoforge/src/client/resources"),
    generatedResources,
    "src/generated/resources",
    generateModMetadata
)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

neoForge {
    version = neoForgeVersion

    mods {
        register("jsmmm") {
            sourceSets["main"]
        }
    }

    ideSyncTask(generateModMetadata)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersion.toInt())
    options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "-A$symbol"))
}

dependencies {
    annotationProcessor("systems.manifold:manifold-preprocessor:2026.1.8")
}

tasks.named<Jar>("jar") {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("${parent?.name}-${project.name}")
}