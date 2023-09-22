plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "rocks.clanattack"
version = "0.0.0"

bukkit {
    main = "rocks.clanattack.impl.entry.Boot"
    version = "0.0.0"
    apiVersion = "1.20"
    name = "Rocks-Core"

    authors = listOf("CheeseTastisch")
    depend = listOf("Rocks-Library")
}

dependencies {
    api(project(":Api"))
    api(kotlin("reflect"))
}

tasks {
    jar {
        dependsOn(":Api:build")

        from(configurations.runtimeClasspath.get()
            .filter { !it.name.contains("kotlin-stdlib") }
            .map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    build {
        doLast {
            project(":Impl").buildDir.resolve("libs")
                .listFiles()
                ?.firstOrNull { !it.nameWithoutExtension.endsWith("-dev") }
                ?.copyTo(rootProject.buildDir.resolve("libs").resolve("Impl.jar"), true)
        }
    }
}