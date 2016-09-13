package org.zalando.fauxpas;

import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

final class LoggingStrategy implements Strategy {

    private final Logger logger;
    private final Strategy strategy;

    LoggingStrategy(final Logger logger, final Strategy strategy) {
        this.logger = logger;
        this.strategy = strategy;
    }

    @Override
    public <X extends Throwable> Runnable adapt(final ThrowingRunnable<X> runnable) {
        return strategy.adapt(() -> {
            try {
                runnable.tryRun();
            } catch (final Throwable e) {
                throw handle(runnable, e);
            }
        });
    }

    @Override
    public <T, X extends Throwable> Supplier<T> adapt(final ThrowingSupplier<T, X> supplier) {
        return strategy.adapt(() -> {
            try {
                return supplier.tryGet();
            } catch (final Throwable e) {
                throw handle(supplier, e);
            }
        });
    }

    @Override
    public <T, X extends Throwable> Consumer<T> adapt(final ThrowingConsumer<T, X> consumer) {
        return strategy.adapt(t -> {
            try {
                consumer.tryAccept(t);
            } catch (final Throwable e) {
                throw handle(consumer, e);
            }
        });
    }

    @Override
    public <T, R, X extends Throwable> Function<T, R> adapt(final ThrowingFunction<T, R, X> function) {
        return strategy.adapt((T t) -> {
            try {
                return function.tryApply(t);
            } catch (final Throwable e) {
                throw handle(function, e);
            }
        });
    }

    @Override
    public <T, X extends Throwable> UnaryOperator<T> adapt(final ThrowingUnaryOperator<T, X> function) {
        return strategy.adapt((T t) -> {
            try {
                return function.tryApply(t);
            } catch (final Throwable e) {
                throw handle(function, e);
            }
        });
    }

    @Override
    public <T, X extends Throwable> Predicate<T> adapt(final ThrowingPredicate<T, X> predicate) {
        return strategy.adapt((T t) -> {
            try {
                return predicate.tryTest(t);
            } catch (final Throwable e) {
                throw handle(predicate, e);
            }
        });
    }

    @Override
    public <T, U, X extends Throwable> BiConsumer<T, U> adapt(final ThrowingBiConsumer<T, U, X> consumer) {
        return strategy.adapt((t, u) -> {
            try {
                consumer.tryAccept(t, u);
            } catch (final Throwable e) {
                throw handle(consumer, e);
            }
        });
    }

    @Override
    public <T, U, R, X extends Throwable> BiFunction<T, U, R> adapt(final ThrowingBiFunction<T, U, R, X> function) {
        return strategy.adapt((T t, U u) -> {
            try {
                return function.tryApply(t, u);
            } catch (final Throwable e) {
                throw handle(function, e);
            }
        });
    }

    @Override
    public <T, X extends Throwable> BinaryOperator<T> adapt(final ThrowingBinaryOperator<T, X> function) {
        return strategy.adapt((T t, T u) -> {
            try {
                return function.tryApply(t, u);
            } catch (final Throwable e) {
                throw handle(function, e);
            }
        });
    }

    @Override
    public <T, U, X extends Throwable> BiPredicate<T, U> adapt(final ThrowingBiPredicate<T, U, X> predicate) {
        return strategy.adapt((T t, U u) -> {
            try {
                return predicate.tryTest(t, u);
            } catch (final Throwable e) {
                throw handle(predicate, e);
            }
        });
    }

    private Throwable handle(final Object source, final Throwable throwable) {
        logger.error("Exception in {}", source, throwable);
        return throwable;
    }

}
