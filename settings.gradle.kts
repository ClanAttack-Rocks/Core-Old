pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Core"
include("Api")
include("Impl")
include("Library")
include("ApiBundle")
include("Test")
