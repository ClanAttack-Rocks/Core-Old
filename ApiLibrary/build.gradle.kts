group = "rocks.clanattack"
version = "0.0.0"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Kotlin
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // Minecraft
    api("com.github.stefvanschie.inventoryframework:IF:0.10.11")
    api("me.catcoder:bukkit-sidebar:6.2.3-SNAPSHOT")
}

tasks{
    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}