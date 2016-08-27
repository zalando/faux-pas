package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Throwable> extends BiFunction<T, U, R> {

    R tryApply(@Nullable T t, @Nullable U u) throws X;

    @Override
    @SneakyThrows
    default R apply(@Nullable final T t, @Nullable final U u) {
        return tryApply(t, u);
    }

}
