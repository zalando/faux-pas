package org.zalando.fauxpas;

import com.google.gag.annotation.remark.ShoutOutTo;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class FauxPas {

    FauxPas() {
        // package private so we can trick code coverage
    }

    public static <X extends Throwable> ThrowingRunnable<X> throwingRunnable(
            final ThrowingRunnable<X> runnable) {
        return runnable;
    }

    public static <X extends Throwable> Runnable throwingRunnable(
            final ThrowingRunnable<X> runnable, final Strategy strategy) {
        return strategy.adapt(runnable);
    }

    public static <T, X extends Throwable> ThrowingSupplier<T, X> throwingSupplier(
            final ThrowingSupplier<T, X> supplier) {
        return supplier;
    }

    public static <T, X extends Throwable> Supplier<T> throwingSupplier(
            final ThrowingSupplier<T, X> supplier, final Strategy strategy) {
        return strategy.adapt(supplier);
    }

    public static <T, X extends Throwable> ThrowingConsumer<T, X> throwingConsumer(
            final ThrowingConsumer<T, X> consumer) {
        return consumer;
    }

    public static <T, X extends Throwable> Consumer<T> throwingConsumer(
            final ThrowingConsumer<T, X> consumer, final Strategy strategy) {
        return strategy.adapt(consumer);
    }

    public static <T, R, X extends Throwable> ThrowingFunction<T, R, X> throwingFunction(
            final ThrowingFunction<T, R, X> function) {
        return function;
    }

    public static <T, R, X extends Throwable> Function<T, R> throwingFunction(
            final ThrowingFunction<T, R, X> function, final Strategy strategy) {
        return strategy.adapt(function);
    }

    public static <T, X extends Throwable> ThrowingPredicate<T, X> throwingPredicate(
            final ThrowingPredicate<T, X> predicate) {
        return predicate;
    }

    public static <T, X extends Throwable> Predicate<T> throwingPredicate(
            final ThrowingPredicate<T, X> predicate, final Strategy strategy) {
        return strategy.adapt(predicate);
    }

    public static <T, R, X extends Throwable> ThrowingBiConsumer<T, R, X> throwingBiConsumer(
            final ThrowingBiConsumer<T, R, X> consumer) {
        return consumer;
    }

    public static <T, R, X extends Throwable> BiConsumer<T, R> throwingBiConsumer(
            final ThrowingBiConsumer<T, R, X> consumer, final Strategy strategy) {
        return strategy.adapt(consumer);
    }

    public static <T, U, R, X extends Throwable> ThrowingBiFunction<T, U, R, X> throwingBiFunction(
            final ThrowingBiFunction<T, U, R, X> function) {
        return function;
    }

    public static <T, U, R, X extends Throwable> BiFunction<T, U, R> throwingBiFunction(
            final ThrowingBiFunction<T, U, R, X> function, final Strategy strategy) {
        return strategy.adapt(function);
    }

    public static <T, U, X extends Throwable> ThrowingBiPredicate<T, U, X> throwingBiPredicate(
            final ThrowingBiPredicate<T, U, X> predicate) {
        return predicate;
    }

    public static <T, U, X extends Throwable> BiPredicate<T, U> throwingBiPredicate(
            final ThrowingBiPredicate<T, U, X> predicate, final Strategy strategy) {
        return strategy.adapt(predicate);
    }

    public static Strategy loggingAnd(final Strategy strategy) {
        return loggingAnd(LoggerFactory.getLogger(LoggingStrategy.class), strategy);
    }

    public static Strategy loggingAnd(final Logger logger, final Strategy strategy) {
        return new LoggingStrategy(logger, strategy);
    }

    public static Strategy ignore() {
        return IgnoreStrategy.INSTANCE;
    }

    public static Strategy rethrow() {
        return DefaultRethrow.INSTANCE;
    }

    static final class DefaultRethrow {
        private static final Strategy INSTANCE = rethrow(unchecked());
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
        private static final Function<Throwable, RuntimeException> INSTANCE = new Function<Throwable, RuntimeException>() {
            @Override
            @SneakyThrows
            public RuntimeException apply(final Throwable throwable) {
                throw throwable;
            }
        };
    }

}
