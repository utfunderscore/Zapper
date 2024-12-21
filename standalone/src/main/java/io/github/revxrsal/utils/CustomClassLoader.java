package io.github.revxrsal.utils;

import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassLoader extends URLClassLoader {

    static {
        registerAsParallelCapable();
    }

    public CustomClassLoader() {
        super("zapper-classloader", new URL[0], ClassLoader.getPlatformClassLoader());
    }

    public void add(URL url) {
        addURL(url);
    }

}
