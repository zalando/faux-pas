package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.fauxpas.FauxPas.throwingBiConsumer;
import static org.zalando.fauxpas.FauxPas.throwingBiFunction;
import static org.zalando.fauxpas.FauxPas.throwingBiPredicate;
import static org.zalando.fauxpas.FauxPas.throwingBinaryOperator;
import static org.zalando.fauxpas.FauxPas.throwingConsumer;
import static org.zalando.fauxpas.FauxPas.throwingFunction;
import static org.zalando.fauxpas.FauxPas.throwingPredicate;
import static org.zalando.fauxpas.FauxPas.throwingRunnable;
import static org.zalando.fauxpas.FauxPas.throwingSupplier;
import static org.zalando.fauxpas.FauxPas.throwingUnaryOperator;

/**
 * Tests the default implementation of e.g. {@link Function#apply(Object)} in {@link ThrowingFunction}.
 */
@RunWith(JUnitPlatform.class)
final class DefaultImplementationTest {

    @SuppressWarnings("ThrowableInstanceNeverThrown") // we're in fact throwing it, multiple times even...
    private final Exception exception = new Exception();

    @Test
    void shouldRethrowExceptionFromRunnable() {
        final ThrowingRunnable<Exception> runnable = throwingRunnable(() -> {
            throw exception;
        });
        shouldThrow(runnable);
    }

    @Test
    void shouldNotRethrowExceptionFromRunnable() throws Exception {
        final ThrowingRunnable<Exception> runnable = throwingRunnable(() -> {
        });
        shouldNotThrow(runnable);
    }

    @Test
    void shouldRethrowExceptionFromSupplier() {
        final ThrowingSupplier<Void, Exception> supplier = throwingSupplier(() -> {
            throw exception;
        });
        shouldThrow(supplier::get);
    }

    @Test
    void shouldNotRethrowExceptionFromSupplier() throws Exception {
        final ThrowingSupplier<Void, Exception> supplier = throwingSupplier(() -> null);
        shouldNotThrow(supplier::get);
    }

    @Test
    void shouldRethrowExceptionFromConsumer() {
        final ThrowingConsumer<Void, Exception> consumer = throwingConsumer($ -> {
            throw exception;
        });
        shouldThrow(() -> consumer.accept(null));
    }

    @Test
    void shouldNotRethrowExceptionFromConsumer() throws Exception {
        final ThrowingConsumer<Void, Exception> consumer = throwingConsumer($ -> {
        });
        shouldNotThrow(() -> consumer.accept(null));
    }

    @Test
    void shouldRethrowExceptionFromFunction() {
        final ThrowingFunction<Void, Void, Exception> function = throwingFunction($ -> {
            throw exception;
        });
        shouldThrow(() -> function.apply(null));
    }

    @Test
    void shouldNotRethrowExceptionFromFunction() throws Exception {
        final ThrowingFunction<Void, Void, Exception> function = throwingFunction((Void $) -> null);
        shouldNotThrow(() -> function.apply(null));
    }

    @Test
    void shouldRethrowExceptionFromUnaryOperator() {
        final ThrowingUnaryOperator<Void, Exception> operator = throwingUnaryOperator($ -> {
            throw exception;
        });
        shouldThrow(() -> operator.apply(null));
    }

    @Test
    void shouldNotRethrowExceptionFromUnaryOperator() throws Exception {
        final ThrowingUnaryOperator<Void, Exception> operator = throwingUnaryOperator((Void $) -> null);
        shouldNotThrow(() -> operator.apply(null));
    }

    @Test
    void shouldRethrowExceptionFromPredicate() {
        final ThrowingPredicate<Void, Exception> predicate = throwingPredicate($ -> {
            throw exception;
        });
        shouldThrow(() -> predicate.test(null));
    }

    @Test
    void shouldNotRethrowExceptionFromPredicate() throws Exception {
        final ThrowingPredicate<Void, Exception> predicate = throwingPredicate((Void $) -> false);
        shouldNotThrow(() -> predicate.test(null));
    }

    @Test
    void shouldRethrowExceptionFromBiConsumer() {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = throwingBiConsumer(($, $2) -> {
            throw exception;
        });
        shouldThrow(() -> consumer.accept(null, null));
    }

    @Test
    void shouldNotRethrowExceptionFromBiConsumer() throws Exception {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = throwingBiConsumer(($, $2) -> {
        });
        shouldNotThrow(() -> consumer.accept(null, null));
    }

    @Test
    void shouldRethrowExceptionFromBiFunction() {
        final ThrowingBiFunction<Void, Void, Void, Exception> function = throwingBiFunction(($, $2) -> {
            throw exception;
        });
        shouldThrow(() -> function.apply(null, null));
    }

    @Test
    void shouldNotRethrowExceptionFromBiFunction() throws Exception {
        final ThrowingBiFunction<Void, Void, Void, Exception> function = throwingBiFunction(($, $2) -> null);
        shouldNotThrow(() -> function.apply(null, null));
    }

    @Test
    void shouldRethrowExceptionFromBinaryOperator() {
        final ThrowingBinaryOperator<Void, Exception> operator = throwingBinaryOperator(($, $2) -> {
            throw exception;
        });
        shouldThrow(() -> operator.apply(null, null));
    }

    @Test
    void shouldNotRethrowExceptionFromBinaryOperator() throws Exception {
        final ThrowingBinaryOperator<Void, Exception> operator = throwingBinaryOperator(($, $2) -> null);
        shouldNotThrow(() -> operator.apply(null, null));
    }

    @Test
    void shouldRethrowExceptionFromBiPredicate() {
        final ThrowingBiPredicate<Void, Void, Exception> predicate = throwingBiPredicate(($, $2) -> {
            throw exception;
        });
        shouldThrow(() -> predicate.test(null, null));
    }

    @Test
    void shouldNotRethrowExceptionFromBiPredicate() throws Exception {
        final ThrowingBiPredicate<Void, Void, Exception> predicate = throwingBiPredicate(($, $2) -> false);
        shouldNotThrow(() -> predicate.test(null, null));
    }

    private void shouldNotThrow(final Runnable nonThrower) {
        nonThrower.run();
    }

    private void shouldThrow(final Runnable thrower) {
        assertThrows(Exception.class, thrower::run);
        assertThat(exception, is(sameInstance(exception)));
    }

}
