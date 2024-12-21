/*
 * This file is part of Zapper, licensed under the MIT License.
 *
 *  Copyright (c) Revxrsal <reflxction.github@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package revxrsal.zapper.util;

import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

/**
 * A utility for accessing the data folder and the plugin.yml information
 * from classloaders directly, without needing {@link org.bukkit.plugin.Plugin}
 * instances.
 */
public final class PluginInfoReader {

    private static final Field description, dataFolder;
    private static final Class<? extends URLClassLoader> PL_CL_LOADER;

    private PluginInfoReader() {
    }

    public static @NotNull PluginDescriptionFile getDescription(@NotNull Class<?> cl) {
        ClassLoader classLoader = cl.getClassLoader();
        if (!PL_CL_LOADER.isAssignableFrom(classLoader.getClass()))
            throw new UnsupportedOperationException("Class is not a plugin class");
        try {
            return (PluginDescriptionFile) description.get(classLoader);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull File getDataFolder(@NotNull Class<?> cl) {
        ClassLoader classLoader = cl.getClassLoader();
        if (!PL_CL_LOADER.isAssignableFrom(classLoader.getClass()))
            throw new UnsupportedOperationException("Class is not a plugin class");
        try {
            return (File) dataFolder.get(classLoader);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            PL_CL_LOADER = Class.forName("org.bukkit.plugin.java.PluginClassLoader")
                    .asSubclass(URLClassLoader.class);
            description = PL_CL_LOADER.getDeclaredField("description");
            description.setAccessible(true);

            dataFolder = PL_CL_LOADER.getDeclaredField("dataFolder");
            dataFolder.setAccessible(true);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
