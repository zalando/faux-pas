package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.BiFunction;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Throwable> extends BiFunction<T, U, R> {

    R tryApply(T t, U u) throws X;

    @Override
    @SneakyThrows
    default R apply(final T t, final U u) {
        return tryApply(t, u);
    }

}
