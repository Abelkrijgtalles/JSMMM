plugins {
    `java-library`
}

val namespace: String by project
val javaVersion: String by project
val symbol: String by project

val sourceSets = extensions.getByType<SourceSetContainer>()

group = rootProject.property("maven_group") as String
version = rootProject.property("mod_version") as String

if (namespace != "official") {
    val remapCommon = registerSourceRemapTask(
        inputDir = rootProject.file("common/src/client/java"),
        targetNamespace = namespace
    )
    sourceSets["main"].java.srcDirs(remapCommon)
} else {
    sourceSets["main"].java.srcDirs(rootProject.file("common/src/client/java"))
}

sourceSets["main"].java.srcDirs(
    parent!!.file("src/main/java"),
    parent!!.file("src/client/java")
)

sourceSets["main"].resources.srcDirs(
    rootProject.file("common/src/client/resources"),
    parent!!.file("src/main/resources"),
    parent!!.file("src/client/resources")
)

extensions.configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xplugin:Manifold")
    options.compilerArgs.addAll(symbol.split(" ").map { "-A$it" })
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE")) { rename { "${it}_JSMMM" } }
}

dependencies {
    annotationProcessor(libs.manifold)
}