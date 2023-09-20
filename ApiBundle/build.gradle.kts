group = "rocks.clanattack"
version = "0.0.0"

dependencies {
    api(project(":Api"))
    api(project(":Library"))
}

tasks.jar {
    dependsOn(":Api:build", ":Library:build")

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}