group = "rocks.clanattack"
version = "0.0.0"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Kotlin
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // Exposed
    api("org.jetbrains.exposed:exposed-core:0.44.0")
    api("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.0")

    // Json
    api("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // Minecraft
    api("com.github.stefvanschie.inventoryframework:IF:0.10.11")
    api("me.catcoder:bukkit-sidebar:6.2.3-SNAPSHOT")

    // Command
    api("cloud.commandframework:cloud-core:1.8.4")
    api("cloud.commandframework:cloud-annotations:1.8.4")
    api("cloud.commandframework:cloud-paper:1.8.4")
    api("cloud.commandframework:cloud-minecraft-extras:1.8.4")

    // Discord
    api("dev.kord:kord-core:0.11.1")
    api("dev.vankka:mcdiscordreserializer:4.3.0")
}

tasks{
    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}