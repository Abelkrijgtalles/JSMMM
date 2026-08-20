plugins {
    id("nl.abelkrijgtalles.jsmmm.neoforge.base")
}

tasks.withType<ProcessResources>().configureEach {
    from(parent!!.file("src/main/templates")) {
        rename("neoforge\\.mods\\.toml", "mods.toml")
    }
}