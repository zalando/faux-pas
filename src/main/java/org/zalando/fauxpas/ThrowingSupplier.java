package org.zalando.fauxpas;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T, X extends Throwable> {

    T tryGet() throws X;

    default Supplier<T> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
