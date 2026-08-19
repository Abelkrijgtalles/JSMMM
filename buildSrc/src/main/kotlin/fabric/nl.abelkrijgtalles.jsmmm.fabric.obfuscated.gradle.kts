plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("nl.abelkrijgtalles.jsmmm.fabric.base")
}

dependencies {
    mappings(loom.officialMojangMappings())
    modImplementation(libs.`fabric-loader`)
}