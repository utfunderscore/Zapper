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
package revxrsal.zapper;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.classloader.URLClassLoaderWrapper;
import revxrsal.zapper.download.ActiveDownload;
import revxrsal.zapper.download.DependencyDownloadException;
import revxrsal.zapper.download.DependencyDownloadResult;
import revxrsal.zapper.download.DownloadManager;
import revxrsal.zapper.relocation.Relocation;
import revxrsal.zapper.relocation.Relocator;
import revxrsal.zapper.repository.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DependencyManager implements DependencyScope {

    public static boolean FAILED_TO_DOWNLOAD = false;
    private static final Pattern COLON = Pattern.compile(":");

    private final File directory;
    private final DownloadManager downloadManager;
    private final URLClassLoaderWrapper loaderWrapper;

    private final List<Dependency> dependencies = new ArrayList<>();
    private final Set<Repository> repositories = new LinkedHashSet<>();
    private final List<Relocation> relocations = new ArrayList<>();

    public DependencyManager(@NotNull File directory, @NotNull DownloadManager downloadManager, @NotNull URLClassLoaderWrapper loaderWrapper) {
        this.directory = directory;
        this.loaderWrapper = loaderWrapper;
        this.downloadManager = downloadManager;
        this.repositories.add(Repository.mavenCentral());
    }

    public void loadParallel() {

        long start = System.currentTimeMillis();
        try {
            directory.mkdirs();

            val pendingDownload = new ArrayList<>(dependencies);

            for (Repository repository : repositories) {

                Map<Dependency, ActiveDownload> activeDownloads = new HashMap<>();

                for (Dependency dep : pendingDownload) {

                    String dependencyName = String.format("%s.%s-%s", dep.getGroupId(), dep.getArtifactId(), dep.getVersion());

                    File file = new File(directory, dependencyName + ".jar");
                    File relocated = new File(directory, dependencyName + "-relocated.jar");
                    if (relocated.exists()) {
                        loaderWrapper.addURL(relocated.toURI().toURL());
                        continue;
                    }

                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            throw new RuntimeException("Failed to download dependency.");
                        }
                    }

                    FileOutputStream fileStream = new FileOutputStream(file);


                    @NotNull ActiveDownload result = downloadManager.download(dep, fileStream, repository);
                    activeDownloads.put(dep, result);
                }

                Stream<CompletableFuture<DependencyDownloadResult>> stream = activeDownloads.values().stream().map(ActiveDownload::getDownloadResultFuture);
                CompletableFuture<Void> downloadCompleteFuture = CompletableFuture.allOf(stream.toArray(CompletableFuture[]::new));
                downloadCompleteFuture.join();

                for (Map.Entry<Dependency, ActiveDownload> entry : activeDownloads.entrySet()) {
                    Dependency dep = entry.getKey();
                    ActiveDownload resultFuture = entry.getValue();

                    DependencyDownloadResult downloadResult = resultFuture.getDownloadResultFuture().join();
                    if (downloadResult.wasSuccessful()) {
                        pendingDownload.remove(dep);

                        File file = new File(directory, String.format("%s.%s-%s.jar", dep.getGroupId(), dep.getArtifactId(), dep.getVersion()));
                        File relocated = new File(directory, String.format("%s.%s-%s-relocated.jar", dep.getGroupId(),
                                dep.getArtifactId(), dep.getVersion()));

                        if (!relocations.isEmpty() && !relocated.exists()) {
                            Relocator.relocate(downloadManager, file, relocated, relocations);
                            file.delete(); // no longer need the original dependency
                            loaderWrapper.addURL(relocated.toURI().toURL());
                        } else {
                            loaderWrapper.addURL(file.toURI().toURL());
                        }
                    }
                }
            }


        } catch (DependencyDownloadException e) {
            if (e.getCause() instanceof UnknownHostException) {
                System.err.println("It appears you do not have an internet connection. Extract the zip in https://bit.ly/3cd3wGe at /Zapper/libraries.");
                FAILED_TO_DOWNLOAD = true;
            } else throw e;
        } catch (RuntimeException d) {
            throw d;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void load() {
        try {
            for (Dependency dep : dependencies) {
                File file = new File(directory, String.format("%s.%s-%s.jar", dep.getGroupId(), dep.getArtifactId(), dep.getVersion()));
                File relocated = new File(directory, String.format("%s.%s-%s-relocated.jar", dep.getGroupId(),
                        dep.getArtifactId(), dep.getVersion()));
                if (relocated.exists()) {
                    loaderWrapper.addURL(relocated.toURI().toURL());
                    continue;
                }
                if (!file.exists()) {
                    List<String> failedRepos = null;
                    for (Repository repository : repositories) {
                        if (!file.exists()) {
                            if (!file.createNewFile()) {
                                throw new RuntimeException("Failed to download dependency.");
                            }
                        }

                        FileOutputStream fileStream = new FileOutputStream(file);


                        DependencyDownloadResult result = downloadManager.download(dep, fileStream, repository).getDownloadResultFuture().join();
                        if (result.wasSuccessful())
                            break;
                        else
                            (failedRepos == null ? failedRepos = new ArrayList<>() : failedRepos).add(repository.toString());
                    }
                    if (failedRepos != null) {
                        throw new DependencyDownloadException(dep, "Could not find dependency in any of the following repositories: " + String.join("\n", failedRepos));
                    }
                }
                if (!relocations.isEmpty() && !relocated.exists()) {
                    Relocator.relocate(downloadManager, file, relocated, relocations);
                    file.delete(); // no longer need the original dependency
                    loaderWrapper.addURL(relocated.toURI().toURL());
                } else {
                    loaderWrapper.addURL(file.toURI().toURL());
                }
            }
        } catch (DependencyDownloadException e) {
            if (e.getCause() instanceof UnknownHostException) {
                System.err.println("It appears you do not have an internet connection. Extract the zip in https://bit.ly/3cd3wGe at /Zapper/libraries.");
                FAILED_TO_DOWNLOAD = true;
            } else throw e;
        } catch (RuntimeException d) {
            throw d;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void dependency(@NotNull Dependency dependency) {
        dependencies.add(dependency);
    }

    public void dependency(String dependency) {
        String[] parts = COLON.split(dependency);
        dependencies.add(new Dependency(parts[0], parts[1], parts[2]));
    }

    public void dependency(String groupId, String artifactId, String version) {
        dependencies.add(new Dependency(groupId, artifactId, version));
    }

    public void relocate(Relocation relocation) {
        relocations.add(relocation);
    }

    public void repository(@NotNull Repository repository) {
        repositories.add(repository);
    }
}
