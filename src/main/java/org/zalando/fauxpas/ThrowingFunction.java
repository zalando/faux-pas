package org.zalando.fauxpas;

import javax.annotation.Nullable;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Throwable> {

    R tryApply(@Nullable T t) throws X;

    default Function<T, R> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
