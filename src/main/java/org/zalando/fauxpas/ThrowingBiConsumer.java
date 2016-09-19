package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, X extends Throwable> extends BiConsumer<T, U> {

    void tryAccept(T t, U u) throws X;

    @Override
    @SneakyThrows
    default void accept(final T t, final U u) {
        tryAccept(t, u);
    }

}
