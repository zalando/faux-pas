package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface ThrowingBiPredicate<T, U, X extends Throwable> extends BiPredicate<T, U> {

    boolean tryTest(@Nullable T t, @Nullable U u) throws X;

    @Override
    @SneakyThrows
    default boolean test(@Nullable final T t, @Nullable final U u) {
        return tryTest(t, u);
    }

    default BiPredicate<T, U> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
