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
    // ApiLibrary
    api(project(":ApiLibrary"))

    // Reflections
    api("io.github.classgraph:classgraph:4.8.162")

    // JSON
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    // Exposed
    api("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    api("com.zaxxer:HikariCP:5.0.1")
    api("org.mariadb.jdbc:mariadb-java-client:3.2.0")

    // Ktor
    api("io.ktor:ktor-client-core:2.3.4")
    api("io.ktor:ktor-client-cio:2.3.4")
    api("io.ktor:ktor-client-websockets:2.3.4")
}

tasks{
    jar {
        dependsOn(":ApiLibrary:build")

        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    build {
        doLast {
            project(":Library").buildDir.resolve("libs")
                .listFiles()
                ?.firstOrNull { !it.nameWithoutExtension.endsWith("-dev") }
                ?.copyTo(rootProject.buildDir.resolve("libs").resolve("library.jar"), true)
        }
    }
}