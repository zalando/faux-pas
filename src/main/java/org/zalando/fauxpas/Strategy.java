package org.zalando.fauxpas;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Strategy {

    <X extends Throwable> Runnable adapt(ThrowingRunnable<X> runnable);

    <T, X extends Throwable> Supplier<T> adapt(ThrowingSupplier<T, X> supplier);

    <T, X extends Throwable> Consumer<T> adapt(ThrowingConsumer<T, X> consumer);

    <T, R, X extends Throwable> Function<T, R> adapt(ThrowingFunction<T, R, X> function);

    <T, X extends Throwable> Predicate<T> adapt(ThrowingPredicate<T, X> consumer);

    <T, U, X extends Throwable> BiConsumer<T, U> adapt(ThrowingBiConsumer<T, U, X> function);

    <T, U, R, X extends Throwable> BiFunction<T, U, R> adapt(ThrowingBiFunction<T, U, R, X> function);

    <T, U, X extends Throwable> BiPredicate<T, U> adapt(ThrowingBiPredicate<T, U, X> function);

}
