package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.Supplier;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingSupplier<T, X extends Throwable> extends Supplier<T> {

    T tryGet() throws X;

    @Override
    @SneakyThrows
    default T get() {
        return tryGet();
    }

}
