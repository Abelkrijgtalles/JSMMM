plugins {
    id("nl.abelkrijgtalles.jsmmm.liteloader")
}

// Forge somehow applies the Minecraft configuration to every project that uses that Forge version, so this stops conflicts with :loaders:forge:1.12
minecraft {
    accessTransformers = rootProject.childProjects["loaders"]?.childProjects["forge"]?.childProjects["1.12"]?.minecraft?.accessTransformers
        ?: files()
}