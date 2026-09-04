plugins {
    id("nl.abelkrijgtalles.jsmmm.forge.obfuscated.nomixin.mods.toml")
}

sourceSets["main"].java.srcDir(parent?.file("src/1.12-1.14.x/java") ?: "")

minecraft {
    useDefaultAccessTransformer()
}