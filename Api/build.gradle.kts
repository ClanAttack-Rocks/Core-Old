plugins {
    id("java")
}

group = "rocks.clanattack"
version = "1.0"

dependencies {
    compileOnlyApi(project(":ApiLibrary"))
}