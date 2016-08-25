package org.zalando.fauxpas;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Throwable> {

    R tryApply(@Nullable T t, @Nullable U u) throws X;

    default BiFunction<T, U, R> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
