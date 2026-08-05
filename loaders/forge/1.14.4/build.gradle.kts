plugins {
    id("nl.abelkrijgtalles.jsmmm.forge-obfuscated-pre-mixin")
}

sourceSets["main"].java.srcDir(parent?.file("src/1.14.x/java") ?: "")