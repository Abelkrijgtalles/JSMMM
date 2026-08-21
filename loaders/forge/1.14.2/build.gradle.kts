plugins {
    id("nl.abelkrijgtalles.jsmmm.forge.obfuscated.nomixin")
}

sourceSets["main"].java.srcDir(parent?.file("src/1.14.x/java") ?: "")

minecraft {
    useDefaultAccessTransformer()
}