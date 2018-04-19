package org.zalando.fauxpas;

import org.apiguardian.api.API;

import java.util.function.UnaryOperator;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingUnaryOperator<T, X extends Throwable> extends ThrowingFunction<T, T, X>, UnaryOperator<T> {

}
