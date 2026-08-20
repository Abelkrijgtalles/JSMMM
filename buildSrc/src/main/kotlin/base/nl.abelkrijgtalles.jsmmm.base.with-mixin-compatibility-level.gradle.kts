val javaVersion: String by project

tasks.withType<ProcessResources>().configureEach {
    filesMatching("jsmmm.client.mixins.json") {
        expand("mixinCompatibilityLevel" to mixinCompatibilityLevelFor(javaVersion))
    }
}