package org.zalando.fauxpas;

import org.apiguardian.api.API;

import java.util.function.BinaryOperator;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingBinaryOperator<T, X extends Throwable> extends ThrowingBiFunction<T, T, T, X>, BinaryOperator<T> {

}
