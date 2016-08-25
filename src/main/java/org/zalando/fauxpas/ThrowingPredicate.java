package org.zalando.fauxpas;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<T, X extends Throwable> {

    boolean tryTest(@Nullable T t) throws X;

    default Predicate<T> with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
