import org.apache.tools.ant.filters.LineContains

plugins {
    id("net.minecraftforge.gradle")
    java
}

val majorForgeVersion: String by project
val forgeVersion: String by project
val javaVersion: String by project
val supportedForgeVersions: String by project
val supportedMinecraftVersions: String by project
val symbol: String by project

group = rootProject.property("maven_group") as String
version = rootProject.property("mod_version") as String

tasks.withType<ProcessResources>().configureEach {
    dependsOn(tasks.compileJava)
    val replaceProperties = mapOf(
        "major_forge_version" to majorForgeVersion,
        "mod_version" to version,
        "java_version" to javaVersion,
        "supported_forge_versions" to supportedForgeVersions,
        "supported_minecraft_versions" to supportedMinecraftVersions,
    )

    inputs.properties(replaceProperties)
    filesMatching("META-INF/mods.toml") {
        expand(replaceProperties)
    }
    into("build/generated/sources/modMetadata")

    filesMatching("jsmmm.client.mixins.json") {
        filter(
            mapOf(
                "negate" to true,
                "contains" to listOf("\"compatibilityLevel\"")
            ),
            LineContains::class.java
        )
    }
}


java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

sourceSets["main"].java.srcDirs(
    rootProject.file("common/src/client/java"),
    rootProject.file("loaders/forge/src/main/java"),
)

sourceSets["main"].resources.srcDirs(
    rootProject.file("common/src/client/resources"),
    rootProject.file("loaders/forge/src/main/resources"),
    "src/generated/resources",
    "build/generated/sources",
)

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    mavenCentral()
}

tasks.named<JavaCompile>("compileJava") {
    options.release.set(javaVersion.toInt())
    options.compilerArgs.addAll(listOf("-Xplugin:Manifold", "-A$symbol"))
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
    annotationProcessor("systems.manifold:manifold-preprocessor:2026.1.8")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["MixinConfigs"] = "jsmmm.client.mixins.json"
    }
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("${parent?.name}-${project.name}")
}