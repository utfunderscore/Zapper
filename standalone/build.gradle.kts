plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly(project(":api"))
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(9))
    }
}