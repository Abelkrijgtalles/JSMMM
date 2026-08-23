plugins {
    id("nl.abelkrijgtalles.jsmmm.forge.obfuscated.mixin")
    id("nl.abelkrijgtalles.jsmmm.liteloader.litemod")
}

renamer.mappings(minecraft.dependency.toObf)

dependencies {
    compileOnly(libs.mixin)
}