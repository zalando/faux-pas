package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, X extends Throwable> extends BiConsumer<T, U> {

    void tryAccept(@Nullable T t, @Nullable U u) throws X;

    @Override
    @SneakyThrows
    default void accept(@Nullable final T t, @Nullable final U u) {
        tryAccept(t, u);
    }

}
