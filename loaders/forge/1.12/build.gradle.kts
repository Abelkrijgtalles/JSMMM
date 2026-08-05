plugins {
    id("nl.abelkrijgtalles.jsmmm.forge-obfuscated-pre-mixin-mcmod")
}

sourceSets["main"].java.srcDir(parent?.file("src/1.12-1.13/java") ?: "")
sourceSets["main"].resources.srcDir(parent?.file("src/1.12-1.13/resources") ?: "")

minecraft {
    accessTransformers = parent?.files("src/1.12-1.13/resources/META-INF/accesstransformer.cfg") ?: files()
}

tasks.jar {
    manifest {
        attributes["FMLAT"] = "accesstransformer.cfg"
    }
}