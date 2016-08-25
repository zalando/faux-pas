package org.zalando.fauxpas;

import com.google.gag.annotation.remark.ShoutOutTo;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

public final class FauxPas {

    FauxPas() {
        // package private so we can trick code coverage
    }

    @SyntacticSugar
    public static <X extends Throwable> ThrowingRunnable<X> throwingRunnable(
            final ThrowingRunnable<X> runnable) {
        return runnable;
    }

    @SyntacticSugar
    public static <T, X extends Throwable> ThrowingSupplier<T, X> throwingSupplier(
            final ThrowingSupplier<T, X> supplier) {
        return supplier;
    }

    @SyntacticSugar
    public static <T, X extends Throwable> ThrowingConsumer<T, X> throwingConsumer(
            final ThrowingConsumer<T, X> consumer) {
        return consumer;
    }

    @SyntacticSugar
    public static <T, R, X extends Throwable> ThrowingFunction<T, R, X> throwingFunction(
            final ThrowingFunction<T, R, X> function) {
        return function;
    }

    @SyntacticSugar
    public static <T, X extends Throwable> ThrowingPredicate<T,X> throwingPredicate(
            final ThrowingPredicate<T, X> predicate) {
        return predicate;
    }

    @SyntacticSugar
    public static <T, R, X extends Throwable> ThrowingBiConsumer<T, R, X> throwingBiConsumer(
            final ThrowingBiConsumer<T, R, X> consumer) {
        return consumer;
    }

    @SyntacticSugar
    public static <T, R, U, X extends Throwable> ThrowingBiFunction<T, R, U, X> throwingBiFunction(
            final ThrowingBiFunction<T, R, U, X> function) {
        return function;
    }

    @SyntacticSugar
    public static <T, U, X extends Throwable> ThrowingBiPredicate<T, U, X> throwingBiPredicate(
            final ThrowingBiPredicate<T, U, X> predicate) {
        return predicate;
    }

    public static Strategy logging() {
        return DefaultLogging.INSTANCE;
    }

    static final class DefaultLogging {
        static final Strategy INSTANCE = logging(LoggerFactory.getLogger(LoggingStrategy.class));
    }

    public static Strategy logging(final Logger logger) {
        return new LoggingStrategy(logger);
    }

    public static Strategy rethrow() {
        return DefaultRethrow.INSTANCE;
    }

    static final class DefaultRethrow {
        static final Strategy INSTANCE = rethrow(unchecked());
    }

    // TODO document that the return value is not required, implementations are free to throw directly
    public static Strategy rethrow(final Function<Throwable, RuntimeException> transformer) {
        return new RethrowStrategy(transformer);
    }

    public static Function<Throwable, RuntimeException> unchecked() {
        return unchecked(RuntimeException::new);
    }

    public static Function<Throwable, RuntimeException> unchecked(
            final Function<Throwable, RuntimeException> transformer) {
        return throwable -> {
            try {
                throw throwable;
            } catch (final Error e) {
                throw e; // TODO returning would be better, but it's impossible
            } catch (final RuntimeException e) {
                return e;
            } catch (final IOException e) {
                return new UncheckedIOException(e);
            } catch (final Throwable e) {
                return transformer.apply(e);
            }
        };
    }

    @ShoutOutTo("http://www.dictionary.com/browse/sneakily")
    public static Function<Throwable, RuntimeException> sneakily() {
        return Sneakily.INSTANCE;
    }

    static final class Sneakily {
        @SuppressWarnings("Convert2Lambda") // we need @SneakyThrows on there
        static final Function<Throwable, RuntimeException> INSTANCE =  new Function<Throwable, RuntimeException>() {
            @Override
            @SneakyThrows
            public RuntimeException apply(final Throwable throwable) {
                throw throwable;
            }
        };
    }

}
