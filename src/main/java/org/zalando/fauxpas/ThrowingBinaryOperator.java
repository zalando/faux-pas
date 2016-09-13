package org.zalando.fauxpas;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface ThrowingBinaryOperator<T, X extends Throwable> extends ThrowingBiFunction<T, T, T, X>, BinaryOperator<T> {

}
