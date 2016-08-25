package org.zalando.fauxpas;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, X extends Throwable> {

    void tryAccept(@Nullable T t, @Nullable U u) throws X;

    default BiConsumer<T, U> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
