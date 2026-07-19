// root build.gradle.kts
val versionData: Map<String, List<String>> = file("versions.properties")
    .readLines()
    .filter { it.isNotBlank() && !it.trim().startsWith("#") }
    .associate { line ->
        val (id, rest) = line.split("=", limit = 2)
        id.trim() to rest.split(",").map { it.trim() }
    }

subprojects {
    apply(plugin = "maven-publish")

    group = rootProject.property("maven_group") as String
    version = rootProject.property("mod_version") as String

    val data = versionData[project.name] ?: return@subprojects
    val (mcVersion, loaderVersion, javaVersion, symbol, obfuscated) = data

    extra["mcVersion"] = mcVersion
    extra["loaderVersion"] = loaderVersion
    extra["javaVersion"] = javaVersion
    extra["symbol"] = symbol
    extra["obfuscated"] = obfuscated.toBoolean()
}
