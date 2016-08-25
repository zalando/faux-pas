package org.zalando.fauxpas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

public final class Strategies {

    Strategies() {
        // package private so we can trick code coverage
    }

    static final class Holder {
        static final Strategy DEFAULT_LOGGING = logging(LoggerFactory.getLogger(LoggingStrategy.class));
        static final Strategy DEFAULT_RETHROW = rethrow(RuntimeException::new);
    }

    public static Strategy logging() {
        return Holder.DEFAULT_LOGGING;
    }

    public static Strategy logging(final Logger logger) {
        return new LoggingStrategy(logger);
    }

    public static Strategy rethrow() {
        return Holder.DEFAULT_RETHROW;
    }

    // TODO document that the return value is not required, implementations are free to throw directly
    public static Strategy rethrow(final Function<Throwable, RuntimeException> transformer) {
        return new RethrowStrategy(transformer);
    }

    // TODO are there some more meaningful conversion we could do here?
    public static Function<Throwable, RuntimeException> unchecked() {
        return throwable -> {
            try {
                throw throwable;
            } catch (final Error e) {
                throw e; // TODO returning would be better
            } catch (final RuntimeException e) {
                return e;
            } catch (final IOException e) {
                return new UncheckedIOException(e);
            } catch (final Throwable e) {
                return new RuntimeException(e);
            }
        };
    }

}
