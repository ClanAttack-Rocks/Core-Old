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
}

tasks.jar {
    dependsOn(":Api:build")

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}