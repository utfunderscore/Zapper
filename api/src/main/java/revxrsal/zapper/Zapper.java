package revxrsal.zapper;

import revxrsal.zapper.classloader.URLClassLoaderWrapper;
import revxrsal.zapper.download.DownloadManager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Executors;

public class Zapper {

    public static void load(final File workingDirectory) {
        load(workingDirectory, new URLClassLoader(new URL[0], Zapper.class.getClassLoader()));
    }

    /**
     * Initializes zapper, downloads dependencies and loads them into the current classloader
     * @param workingDirectory The working directory for your application, used to create the libraries folder
     */
    public static void load(final File workingDirectory, final URLClassLoader classLoader) {

        RuntimeLibPluginConfiguration config = RuntimeLibPluginConfiguration.parse();
        File dependenciesFolder = new File(workingDirectory, config.getLibsFolder());

        if (!dependenciesFolder.exists()) {
            System.out.println("It appears you're running this application for the first time.");
            System.out.println("Please give wait a few seconds to install dependencies. This is a one-time process.");
        }

        DownloadManager downloadManager = new DownloadManager(Executors.newFixedThreadPool(15));

        DependencyManager dependencyManager = new DependencyManager(
                dependenciesFolder,
                downloadManager,
                URLClassLoaderWrapper.wrap(classLoader)
        );
        config.getDependencies().forEach(dependencyManager::dependency);
        config.getRepositories().forEach(dependencyManager::repository);
        config.getRelocations().forEach(dependencyManager::relocate);
        dependencyManager.loadParallel();
    }
}
