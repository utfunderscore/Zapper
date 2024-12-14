plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.1.0"
}

version = "1.0.0"

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
            implementationClass = "revxrsal.zapper.gradle.ZapperPlugin"
        }
    }
}
