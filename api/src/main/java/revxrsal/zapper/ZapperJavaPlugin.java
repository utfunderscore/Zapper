package revxrsal.zapper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.zapper.classloader.URLClassLoaderWrapper;
import revxrsal.zapper.util.ClassLoaderReader;

import java.io.File;
import java.net.URLClassLoader;

/**
 * An extension of {@link JavaPlugin} that downloads dependencies at runtime.
 * <p>
 * This should only be used in tandem with the Gradle plugin! Please consult
 * the documentation otherwise.
 */
public abstract class ZapperJavaPlugin extends JavaPlugin {

    static {
        RuntimeLibPluginConfiguration config = RuntimeLibPluginConfiguration.parse();
        File libraries = new File(ClassLoaderReader.getDataFolder(ZapperJavaPlugin.class), config.getLibsFolder());
        if (!libraries.exists()) {
            PluginDescriptionFile pdf = ClassLoaderReader.getDescription(ZapperJavaPlugin.class);
            // "ur plugin slow!!"
            Bukkit.getLogger().info("[" + pdf.getName() + "] It appears you're running " + pdf.getName() + " for the first time.");
            Bukkit.getLogger().info("[" + pdf.getName() + "] Please give me a few seconds to install dependencies. This is a one-time process.");
        }
        DependencyManager dependencyManager = new DependencyManager(
                libraries,
                URLClassLoaderWrapper.wrap((URLClassLoader) ZapperJavaPlugin.class.getClassLoader())
        );
        config.getDependencies().forEach(dependencyManager::dependency);
        config.getRepositories().forEach(dependencyManager::repository);
        config.getRelocations().forEach(dependencyManager::relocate);
        dependencyManager.load();
    }
}