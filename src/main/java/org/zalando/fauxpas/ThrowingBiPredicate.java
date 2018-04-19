package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.BiPredicate;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingBiPredicate<T, U, X extends Throwable> extends BiPredicate<T, U> {

    boolean tryTest(T t, U u) throws X;

    @Override
    @SneakyThrows
    default boolean test(final T t, final U u) {
        return tryTest(t, u);
    }

}
