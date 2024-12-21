package revxrsal.zapper.download;

import java.util.concurrent.CompletableFuture;

public class ActiveDownload {

    private final CompletableFuture<DependencyDownloadResult> downloadResultFuture;
    private final int size;

    public ActiveDownload(CompletableFuture<DependencyDownloadResult> downloadResultFuture, int size) {
        this.downloadResultFuture = downloadResultFuture;
        this.size = size;
    }

    public CompletableFuture<DependencyDownloadResult> getDownloadResultFuture() {
        return downloadResultFuture;
    }

    public int getSize() {
        return size;
    }
}
