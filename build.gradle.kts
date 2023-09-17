import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version "1.7.22"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "rocks.clanattack"
version = "0.0.0"

bukkit {
    main = "rocks.clanattack.impl.entry.Boot"
    version = "0.0.0"
    apiVersion = "1.20"
    name = "ClanAttack Rocks (Core-API)"

    authors = listOf("CheeseTastisch")
    libraries = listOf(
        "com.fasterxml.jackson.core:jackson-databind:2.15.2",
        "com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2",
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2",

        "org.jetbrains.kotlin:kotlin-reflect:1.7.22",
        "org.reflections:reflections:0.9.12"
    )
}

dependencies {
    implementation(project(":Api"))
    implementation(project(":Impl"))
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "net.minecrell.plugin-yml.bukkit")

    repositories {
        mavenCentral()
    }

    dependencies {
        paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    }

    kotlin {
        jvmToolchain(17)
    }

    tasks {
        assemble {
            dependsOn(reobfJar)
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }

        jar {
            manifest {
                attributes.clear()
            }

            from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = listOf("-Xjvm-default=enable")
            }
        }
    }
}