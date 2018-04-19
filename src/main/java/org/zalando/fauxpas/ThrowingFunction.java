package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.util.function.Function;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Throwable> extends Function<T, R> {

    R tryApply(T t) throws X;

    @Override
    @SneakyThrows
    default R apply(final T t) {
        return tryApply(t);
    }

}
