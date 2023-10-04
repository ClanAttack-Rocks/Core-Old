plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "rocks.clanattack"
version = "0.0.0"

bukkit {
    main = "rocks.clanattack.test.Boot"
    version = "0.0.0"
    apiVersion = "1.20"
    name = "Rocks-Test"

    authors = listOf("CheeseTastisch")
    depend = listOf("Rocks-Core")
}

dependencies {
    compileOnlyApi(project(":Api"))
}

tasks {
    build {
        doLast {
            project(":Test").buildDir.resolve("libs")
                .listFiles()
                ?.firstOrNull { !it.nameWithoutExtension.endsWith("-dev") }
                ?.copyTo(rootProject.buildDir.resolve("libs").resolve("test.jar"), true)
        }
    }
}