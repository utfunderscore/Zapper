package io.github.revxrsal;

import io.github.revxrsal.utils.CustomClassLoader;
import revxrsal.zapper.Zapper;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.CodeSource;

public class ZapperStandalone {

    private ZapperStandalone() {
    }

    /**
     * Creates a new instance of your application with zapper loaded dependencies
     */
    public static void inject(final String entryPointClass) throws RuntimeException {
        // Create new custom classloader that allows loading from URL's
        CustomClassLoader classLoader = new CustomClassLoader();

        // Re-add files from current jar file as they are not in the created classloader
        CodeSource src = ZapperStandalone.class.getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();
        classLoader.add(jar);

        // Load zapper dependencies
        Zapper.load(new File(System.getProperty("user.dir")), classLoader);
        Thread.currentThread().setContextClassLoader(classLoader);


        // Create new instance of your application using the custom classloader
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(entryPointClass);
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.trySetAccessible();
            constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
