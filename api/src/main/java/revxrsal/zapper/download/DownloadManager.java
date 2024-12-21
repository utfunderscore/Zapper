package revxrsal.zapper.download;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.Dependency;
import revxrsal.zapper.repository.Repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class DownloadManager {

    private @NotNull
    final ExecutorService executor;

    public DownloadManager(@NotNull ExecutorService executor) {
        this.executor = executor;
    }

    @CheckReturnValue
    public @NotNull ActiveDownload download(Dependency dependency, OutputStream outputStream, @NotNull Repository repository) {
        try {
            URL url = repository.resolve(dependency);

            System.out.println(url.toExternalForm());

            URLConnection connection = url.openConnection();

            int length = connection.getContentLength();

            return new ActiveDownload(CompletableFuture.supplyAsync(new DownloadTask(connection, outputStream), executor), length);
        } catch (Exception e) {
            throw new DependencyDownloadException(dependency, e);
        }
    }

    public static final class DownloadTask implements Supplier<DependencyDownloadResult> {

        private final @NotNull URLConnection urlConnection;
        private final @NotNull OutputStream outputStream;

        public DownloadTask(@NotNull URLConnection urlConnection, @NotNull OutputStream outputStream) {
            this.urlConnection = urlConnection;
            this.outputStream = outputStream;
        }

        @Override
        public DependencyDownloadResult get() {
            try {
                try (InputStream depIn = urlConnection.getInputStream()) {
                    try (OutputStream outStream = outputStream) {
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = depIn.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                return DependencyDownloadResult.success();
            } catch (Throwable t) {
                return DependencyDownloadResult.failure(t);
            }
        }
    }

}
