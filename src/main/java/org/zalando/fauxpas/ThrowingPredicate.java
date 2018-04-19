package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingPredicate<T, X extends Throwable> extends Predicate<T> {

    boolean tryTest(T t) throws X;

    @Override
    @SneakyThrows
    default boolean test(final T t) {
        return tryTest(t);
    }

}
