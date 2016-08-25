package org.zalando.fauxpas;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface ThrowingBiPredicate<T, U, X extends Throwable> {

    boolean tryTest(@Nullable T t, @Nullable U u) throws X;

    default BiPredicate<T, U> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
