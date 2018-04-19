package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingConsumer<T, X extends Throwable> extends Consumer<T> {

    void tryAccept(T t) throws X;

    @Override
    @SneakyThrows
    default void accept(final T t) {
        tryAccept(t);
    }

}
