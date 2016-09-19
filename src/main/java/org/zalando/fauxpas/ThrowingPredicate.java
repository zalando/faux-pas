package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<T, X extends Throwable> extends Predicate<T> {

    boolean tryTest(T t) throws X;

    @Override
    @SneakyThrows
    default boolean test(final T t) {
        return tryTest(t);
    }

}
