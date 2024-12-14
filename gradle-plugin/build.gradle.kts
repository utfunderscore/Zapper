plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
}

version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:8.1.1")
}

gradlePlugin {
    plugins {
        create("zapper") {
            id = "io.github.revxrsal.zapper"
            displayName = "Zapper"
            implementationClass = "revxrsal.zapper.gradle.ZapperPlugin"
            website = "https://github.com/Revxrsal/Zapper"
            vcsUrl = "https://github.com/Revxrsal/Zapper"
            tags = listOf("maven", "downloader", "runtime dependency")
        }
    }
}
