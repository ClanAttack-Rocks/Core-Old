plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "rocks.clanattack"
version = "0.0.0"

bukkit {
    main = "rocks.clanattack.library.Library"
    version = "0.0.0"
    apiVersion = "1.20"
    name = "Rocks-Library"

    authors = listOf("CheeseTastisch")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // JSON
    api("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    // Kotlin
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // Reflections
    api("io.github.classgraph:classgraph:4.8.162")

    // Minecraft
    api("com.github.stefvanschie.inventoryframework:IF:0.10.11")
    api("me.catcoder:bukkit-sidebar:6.2.3-SNAPSHOT")
}

tasks{
    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    build {
        doLast {
            project(":Library").buildDir.resolve("libs")
                .listFiles()
                ?.firstOrNull { !it.nameWithoutExtension.endsWith("-dev") }
                ?.copyTo(rootProject.buildDir.resolve("libs").resolve("Library.jar"), true)
        }
    }
}