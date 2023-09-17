plugins {
    id("java")
}

group = "rocks.clanattack"
version = "0.0.0"

dependencies {
    compileOnlyApi("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    compileOnlyApi("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    compileOnlyApi("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    compileOnlyApi(kotlin("reflect"))
    compileOnlyApi("org.reflections:reflections:0.9.12")
}