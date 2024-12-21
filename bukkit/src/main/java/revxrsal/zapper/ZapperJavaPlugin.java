package revxrsal.zapper;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.zapper.util.PluginInfoReader;


import java.net.URLClassLoader;

/**
 * An extension of {@link JavaPlugin} that downloads dependencies at runtime.
 * <p>
 * This should only be used in tandem with the Gradle plugin! Please consult
 * the documentation otherwise.
 */
public abstract class ZapperJavaPlugin extends JavaPlugin {

    static {
        Zapper.load(PluginInfoReader.getDataFolder(ZapperJavaPlugin.class), (URLClassLoader) ZapperJavaPlugin.class.getClassLoader());
    }
}