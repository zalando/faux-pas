package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Throwable> extends Function<T, R> {

    R tryApply(T t) throws X;

    @Override
    @SneakyThrows
    default R apply(final T t) {
        return tryApply(t);
    }

}
