package org.zalando.fauxpas;

import lombok.SneakyThrows;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T, X extends Throwable> extends Supplier<T> {

    T tryGet() throws X;

    @Override
    @SneakyThrows
    default T get() {
        return tryGet();
    }

    default Supplier<T> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
