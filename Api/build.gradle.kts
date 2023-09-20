plugins {
    id("java")
}

group = "rocks.clanattack"
version = "0.0.0"

dependencies {
    compileOnlyApi(project(":Library"))
}