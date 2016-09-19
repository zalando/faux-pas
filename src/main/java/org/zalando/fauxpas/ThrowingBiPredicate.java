package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface ThrowingBiPredicate<T, U, X extends Throwable> extends BiPredicate<T, U> {

    boolean tryTest(T t, U u) throws X;

    @Override
    @SneakyThrows
    default boolean test(final T t, final U u) {
        return tryTest(t, u);
    }

}
