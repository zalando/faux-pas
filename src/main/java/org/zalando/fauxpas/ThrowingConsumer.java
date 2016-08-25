package org.zalando.fauxpas;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, X extends Throwable> {

    void tryAccept(@Nullable T t) throws X;

    default Consumer<T> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
