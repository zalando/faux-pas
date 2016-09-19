package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, X extends Throwable> extends Consumer<T> {

    void tryAccept(T t) throws X;

    @Override
    @SneakyThrows
    default void accept(final T t) {
        tryAccept(t);
    }

}
