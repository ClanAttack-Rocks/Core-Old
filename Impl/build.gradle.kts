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
    libraries = listOf(
        "com.fasterxml.jackson.core:jackson-databind:2.15.2",
        "com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2",
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2",

        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2",
        "org.reflections:reflections:0.9.12"
    )
}

dependencies {
    api(project(":Api"))
}

tasks {
    jar {
        dependsOn(":Api:build")

        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}