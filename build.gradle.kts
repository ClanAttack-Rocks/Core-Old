import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version "1.9.0"
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "rocks.clanattack"
version = "1.0"

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "io.papermc.paperweight.userdev")

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

        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
}

tasks {
    register("buildProject") {
        dependsOn(":Library:build", ":Impl:build")
    }
}