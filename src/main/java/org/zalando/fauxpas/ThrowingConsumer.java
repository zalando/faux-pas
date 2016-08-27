package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, X extends Throwable> extends Consumer<T> {

    void tryAccept(@Nullable T t) throws X;

    @Override
    @SneakyThrows
    default void accept(@Nullable final T t) {
        tryAccept(t);
    }

}
