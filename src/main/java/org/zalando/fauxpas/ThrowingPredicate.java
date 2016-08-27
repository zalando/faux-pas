package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<T, X extends Throwable> extends Predicate<T> {

    boolean tryTest(@Nullable T t) throws X;

    @Override
    @SneakyThrows
    default boolean test(@Nullable final T t) {
        return tryTest(t);
    }

}
