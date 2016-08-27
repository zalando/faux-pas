package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Throwable> extends Function<T, R> {

    R tryApply(@Nullable T t) throws X;

    @Override
    @SneakyThrows
    default R apply(@Nullable final T t) {
        return tryApply(t);
    }

}
