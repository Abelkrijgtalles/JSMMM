import org.apache.tools.ant.filters.LineContains

plugins {
    id("nl.abelkrijgtalles.jsmmm.base")
    id("net.minecraftforge.gradle")
}

val forgeVersion: String by project

tasks.withType<ProcessResources>().configureEach {
    dependsOn(tasks.compileJava)
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

sourceSets["main"].resources.srcDirs(
    "build/generated/sources",
)

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
}