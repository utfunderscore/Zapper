package revxrsal.zapper.repository;

import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.Dependency;

import java.io.File;
import java.net.URL;

/**
 * Represents the local maven repository
 */
final class LocalMavenRepository implements Repository {

    private final @NotNull File directory;

    LocalMavenRepository(@NotNull File directory) {
        this.directory = directory;
    }

    @Override
    public @NotNull URL resolve(@NotNull Dependency dependency) throws Exception {
        String systemPath = dependency.getMavenPath().replace('/', File.separatorChar);
        File file = new File(directory, systemPath);
        return file.toURI().toURL();
    }

    @Override
    public String toString() {
        return directory.toString();
    }
}
