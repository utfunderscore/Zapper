package revxrsal.zapper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the result of downolading a dependency from some repository
 */
public interface DependencyDownloadResult {

    /**
     * Whether it was successful or not
     *
     * @return if the download was successful or not
     */
    boolean wasSuccessful();

    @Contract("-> this")
    default @NotNull Failure asFailure() {
        if (this instanceof Failure)
            return (Failure) this;
        throw new IllegalArgumentException("Dependency was downloaded successfully.");
    }

    @Contract(pure = true)
    static @NotNull Success success() {
        return Success.INSTANCE;
    }

    static @NotNull Failure failure(@NotNull Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable cannot be null!");
        return new Failure(throwable);
    }

    final class Success implements DependencyDownloadResult {

        private static final Success INSTANCE = new Success();

        @Override
        public boolean wasSuccessful() {
            return true;
        }
    }

    final class Failure implements DependencyDownloadResult {

        private final Throwable throwable;

        public Failure(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public boolean wasSuccessful() {
            return false;
        }

        public @NotNull Throwable getError() {
            return throwable;
        }

    }

}
