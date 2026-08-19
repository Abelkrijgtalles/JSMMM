fun mixinCompatibilityLevelFor(javaVersion: String): String {
    val level = javaVersion.toInt()
    return "JAVA_$level"
}