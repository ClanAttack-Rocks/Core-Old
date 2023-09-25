group = "rocks.clanattack"
version = "0.0.0"

dependencies {
    api(project(":Api"))
    api(project(":ApiLibrary"))
}

tasks {
    jar {
        dependsOn(":Api:build", ":ApiLibrary:build")

        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    build {
        doLast {
            project(":ApiBundle").buildDir.resolve("libs")
                .listFiles()
                ?.firstOrNull { !it.nameWithoutExtension.endsWith("-dev") }
                ?.copyTo(rootProject.buildDir.resolve("libs").resolve("Api.jar"), true)
        }
    }
}