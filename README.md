# Zapper

[![Discord](https://discord.com/api/guilds/939962855476846614/widget.png)](https://discord.gg/pEGGF785zp)
[![Maven Central](https://img.shields.io/maven-metadata/v/https/repo1.maven.org/maven2/io/github/revxrsal/zapper.api/maven-metadata.xml.svg?label=maven%20central&colorB=brightgreen)](https://search.maven.org/artifact/io.github.revxrsal/zapper.api)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build](https://github.com/Revxrsal/Zapper/actions/workflows/build.yml/badge.svg)](https://github.com/Revxrsal/Zapper/actions/workflows/build.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/revxrsal/zapper/badge)](https://www.codefactor.io/repository/github/revxrsal/zapper)

Zapper is a powerful runtime dependency downloader for Bukkit that aims to abstract away all difficulties that arise
with such task.

## Features

- 🗺️ **Relocation**: Relocate dependencies to avoid conflicts with other plugins or Bukkit's own dependencies
- 🚌 **Transitive dependencies**: Dependencies of dependencies will also be downloaded to ensure no classes are missing
  at runtime
- 🔥 **Does not require `org.bukkit.plugin.Plugin` instances**: This is a very significant convenience of Zapper, as it
  allows you to:
    - Not have to worry about loading dependencies before or after your plugin loads.
    - Your main class can use dependency classes freely (e.g. download Kotlin and your main class would be in Kotlin)
    - Dependencies are loaded before your plugin loads
- 🐘 Seamless integration with Gradle and ShadowJar
- 🍵 Works on all Java versions from 8 to 22

## Usage

### With Gradle plugin (recommended)

Zapper provides a Gradle plugin that seamlessly integrates with your build script. To apply it:

build.gradle:

```groovy
plugins {
    id 'io.github.revxrsal.zapper' version '1.0.0'
}
```

build.gradle.kts:

```groovy
plugins {
    id("io.github.revxrsal.zapper") version("1.0.0")
}
```

Then, add your dependencies using the `zap` configuration:

```groovy
dependencies {
    // an example dependency
    zap("com.squareup.moshi:moshi:1.15.2")

    // use the dependency notation as you would with other dependencies
    zap("com.squareup.moshi", "moshi", "1.15.2")
}
```

You can provide additional configuration using the `zapper` extension:

```groovy
zapper {
    // directory to download dependencies in
    libsFolder = "libraries"

    // repositories to fetch dependencies from
    // 
    // by default: includes maven central
    repositories {

        // example repository
        maven("https://jitpack.io/")

        // optional: use all repositories declared in this
        // file if you don't want to re-include everything here
        useProjectRepositories()
    }
}
```

Then, in your plugin, extend `ZapperJavaPlugin` instead of `JavaPlugin`:

```java
public final class MyPlugin extends ZapperJavaPlugin {

    // use your dependencies here! don't think twice :D
    private final Moshi moshi;

}
```

### With Maven

Unfortunately, Maven usage is not as smooth as the Gradle one. However, you can use the API
provided by Zapper to download dependencies at runtime:

To add the Zapper API:

```xml

<dependency>
    <groupId>io.github.revxrsal</groupId>
    <artifactId>zapper.api</artifactId>
    <version>0.0.1</version>
    <scope>compile</scope>
</dependency>
```

See [Zapper dependency API usage](#dependency-api-usage)

## Dependency API usage

1. Create a **base** class that declares your dependencies. Don't include your actual plugin logic here. Your main plugin class should extend this base class.

```java
public abstract class MyPluginBase extends JavaPlugin { 

    static {
        File libraries = new File(
                ClassLoaderReader.getDataFolder(MyPluginBase.class),
                "libraries" // libraries folder
        );
        if (!libraries.exists()) {
            PluginDescriptionFile pdf = ClassLoaderReader.getDescription(ZapperJavaPlugin.class);
            Bukkit.getLogger().info("[" + pdf.getName() + "] It appears you're running " + pdf.getName() + " for the first time.");
            Bukkit.getLogger().info("[" + pdf.getName() + "] Please give me a few seconds to install dependencies. This is a one-time process.");
        }
        DependencyManager dependencyManager = new DependencyManager(
                libraries,
                URLClassLoaderWrapper.wrap((URLClassLoader) MyPluginBase.class.getClassLoader())
        );

        // add your repositories
        dependencyManager.repository(Repository.mavenCentral());
        dependencyManager.repository(Repository.maven("https://jitpack.io"));

        // add your dependencies
        dependencyManager.dependency("com.squareup.moshi:moshi:1.15.2");

        // IMPORTANT NOTE: Beware that this path may get relocated/changed
        // by your build tool!!! Escape it using runtime tricks if necessary
        dependencyManager.relocate(new Relocation(
                "com{}squareup{}moshi".replace("{}", "."),
                "myplugin.moshi"
        ));

        dependencyManager.load();
    }
}
```

2. Create your actual main class and extend the base class you have just defined:

```java
public final class MyPlugin extends MyPluginBase {
    // your use dependencies here as you like
}
```

### Extending custom JavaPlugin classes
If you, for some reason, would like to extend a different class than a JavaPlugin, and is using the Gradle plugin, you can avoid extending the ZapperJavaPlugin class by doing the following:
1. Declare a **base** class that extends the custom JavaPlugin class:
```java
public abstract class MyPluginBase extends CustomJavaPlugin {
    
}
```

2. Add the following static block to `MyPluginBase`:
```java
    static {
        RuntimeLibPluginConfiguration config = RuntimeLibPluginConfiguration.parse();
        File libraries = new File(ClassLoaderReader.getDataFolder(MyPluginBase.class), config.getLibsFolder());
        if (!libraries.exists()) {
            PluginDescriptionFile pdf = ClassLoaderReader.getDescription(MyPluginBase.class);
            Bukkit.getLogger().info("[" + pdf.getName() + "] It appears you're running " + pdf.getName() + " for the first time.");
            Bukkit.getLogger().info("[" + pdf.getName() + "] Please give me a few seconds to install dependencies. This is a one-time process.");
        }
        DependencyManager dependencyManager = new DependencyManager(
                libraries,
                URLClassLoaderWrapper.wrap((URLClassLoader) MyPluginBase.class.getClassLoader())
        );
        config.getDependencies().forEach(dependencyManager::dependency);
        config.getRepositories().forEach(dependencyManager::repository);
        config.getRelocations().forEach(dependencyManager::relocate);
        dependencyManager.load();
    }
```
3. Extend `MyPluginBase`
```java
public final class MyPlugin extends MyPluginBase {
    // use your dependencies here as you like!
}
```

## Sponsors

If Zapper has made your life significantly easier or you're feeling particularly generous, consider sponsoring the
project! It's a great way to support the many hours I've spent maintaining this library and keeps me motivated. Please
don't sponsor if you can't afford it.

[Donate with PayPal](https://www.paypal.me/Recxrsion)

Huge thanks to those who donated! 😄
