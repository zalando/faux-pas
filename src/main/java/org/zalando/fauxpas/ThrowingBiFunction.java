package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Throwable> extends BiFunction<T, U, R> {

    R tryApply(T t, U u) throws X;

    @Override
    @SneakyThrows
    default R apply(final T t, final U u) {
        return tryApply(t, u);
    }

}
