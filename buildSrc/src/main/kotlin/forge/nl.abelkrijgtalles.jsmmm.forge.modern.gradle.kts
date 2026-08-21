plugins {
    id("nl.abelkrijgtalles.jsmmm.forge.base")
    id("nl.abelkrijgtalles.jsmmm.forge.mods.toml")
}

tasks.jar {
    manifest {
        attributes["MixinConfigs"] = "jsmmm.client.mixins.json"
    }
}