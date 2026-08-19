plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("ploceus")
    id("nl.abelkrijgtalles.jsmmm.fabric.base")
}

val featherBuild: String by project

ploceus {
    setIntermediaryGeneration(2)
}

dependencies {
    mappings(ploceus.featherMappings(featherBuild))
    modImplementation(libs.`fabric-loader`)
}