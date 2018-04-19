package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.BiConsumer;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, X extends Throwable> extends BiConsumer<T, U> {

    void tryAccept(T t, U u) throws X;

    @Override
    @SneakyThrows
    default void accept(final T t, final U u) {
        tryAccept(t, u);
    }

}
