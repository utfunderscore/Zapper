package revxrsal.zapper.download;

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

    /**
     * Returns this {@link DependencyDownloadResult} as a {@link Failure}
     *
     * @return The failure
     */
    @Contract("-> this")
    default @NotNull Failure asFailure() {
        if (this instanceof Failure)
            return (Failure) this;
        throw new IllegalArgumentException("Dependency was downloaded successfully.");
    }

    /**
     * Represents a successful dependency download
     *
     * @return The success result
     */
    @Contract(pure = true)
    static @NotNull Success success() {
        return Success.INSTANCE;
    }

    /**
     * Represents a failed dependency download
     *
     * @param throwable The error
     * @return The failure result
     */
    static @NotNull Failure failure(@NotNull Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable cannot be null!");
        return new Failure(throwable);
    }

    final class Success implements DependencyDownloadResult {

        private static final Success INSTANCE = new Success();

        private Success() {
        }

        @Override
        public boolean wasSuccessful() {
            return true;
        }
    }

    final class Failure implements DependencyDownloadResult {

        private final Throwable throwable;

        Failure(Throwable throwable) {
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
