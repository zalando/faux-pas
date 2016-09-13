package org.zalando.fauxpas;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

enum IgnoreStrategy implements Strategy {

    INSTANCE;

    @Override
    public <X extends Throwable> Runnable adapt(final ThrowingRunnable<X> runnable) {
        return () -> {
            try {
                runnable.tryRun();
            } catch (final Throwable e) {
                ignore(e);
            }
        };
    }

    @Override
    public <T, X extends Throwable> Supplier<T> adapt(final ThrowingSupplier<T, X> supplier) {
        return () -> {
            try {
                return supplier.tryGet();
            } catch (final Throwable e) {
                ignore(e);
                return null;
            }
        };
    }

    @Override
    public <T, X extends Throwable> Consumer<T> adapt(final ThrowingConsumer<T, X> consumer) {
        return t -> {
            try {
                consumer.tryAccept(t);
            } catch (final Throwable e) {
                ignore(e);
            }
        };
    }

    @Override
    public <T, R, X extends Throwable> Function<T, R> adapt(final ThrowingFunction<T, R, X> function) {
        return t -> apply(function, t);
    }

    @Override
    public <T, X extends Throwable> UnaryOperator<T> adapt(final ThrowingUnaryOperator<T, X> operator) {
        return t -> apply(operator::tryApply, t);
    }

    private  <T, R, X extends Throwable> R apply(final ThrowingFunction<T, R, X> function, final T t) {
        try {
            return function.tryApply(t);
        } catch (final Throwable e) {
            ignore(e);
            return null;
        }
    }


    @Override
    public <T, X extends Throwable> Predicate<T> adapt(final ThrowingPredicate<T, X> predicate) {
        return t -> {
            try {
                return predicate.tryTest(t);
            } catch (final Throwable e) {
                ignore(e);
                return false;
            }
        };
    }

    @Override
    public <T, U, X extends Throwable> BiConsumer<T, U> adapt(final ThrowingBiConsumer<T, U, X> consumer) {
        return (t, u) -> {
            try {
                consumer.tryAccept(t, u);
            } catch (final Throwable e) {
                ignore(e);
            }
        };
    }

    @Override
    public <T, U, R, X extends Throwable> BiFunction<T, U, R> adapt(final ThrowingBiFunction<T, U, R, X> function) {
        return (t, u) -> apply(function, t, u);
    }

    @Override
    public <T, X extends Throwable> BinaryOperator<T> adapt(final ThrowingBinaryOperator<T,X> function) {
        return (t, u) -> apply(function::tryApply, t, u);
    }

    private  <T, U, R, X extends Throwable> R apply(final ThrowingBiFunction<T, U, R, X> function, final T t, final U u) {
        try {
            return function.tryApply(t, u);
        } catch (final Throwable e) {
            ignore(e);
            return null;
        }
    }

    @Override
    public <T, U, X extends Throwable> BiPredicate<T, U> adapt(final ThrowingBiPredicate<T, U, X> predicate) {
        return (t, u) -> {
            try {
                return predicate.tryTest(t, u);
            } catch (final Throwable e) {
                ignore(e);
                return false;
            }
        };
    }

    private void ignore(final Throwable throwable) {
        // nothing to do...
    }

}
