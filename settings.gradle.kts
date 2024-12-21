rootProject.name = "zapper"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
include("api")
include("gradle-plugin")
include("bukkit")
include("standalone")
