package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
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
public final class DefaultImplementationTest {

    @SuppressWarnings("ThrowableInstanceNeverThrown") // we're in fact throwing it, multiple times even...
    private final Exception exception = new Exception();

    @Test
    public void shouldRethrowExceptionFromRunnable() {
        final ThrowingRunnable<Exception> runnable = throwingRunnable(() -> {
            throw exception;
        });
        shouldThrow(runnable);
    }

    @Test
    public void shouldNotRethrowExceptionFromRunnable() throws Exception {
        final ThrowingRunnable<Exception> runnable = throwingRunnable(() -> {
        });
        shouldNotThrow(runnable);
    }

    @Test
    public void shouldRethrowExceptionFromSupplier() {
        final ThrowingSupplier<Void, Exception> supplier = throwingSupplier(() -> {
            throw exception;
        });
        shouldThrow(supplier::get);
    }

    @Test
    public void shouldNotRethrowExceptionFromSupplier() throws Exception {
        final ThrowingSupplier<Void, Exception> supplier = throwingSupplier(() -> null);
        shouldNotThrow(supplier::get);
    }

    @Test
    public void shouldRethrowExceptionFromConsumer() {
        final ThrowingConsumer<Void, Exception> consumer = throwingConsumer($ -> {
            throw exception;
        });
        shouldThrow(() -> consumer.accept(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromConsumer() throws Exception {
        final ThrowingConsumer<Void, Exception> consumer = throwingConsumer($ -> {
        });
        shouldNotThrow(() -> consumer.accept(null));
    }

    @Test
    public void shouldRethrowExceptionFromFunction() {
        final ThrowingFunction<Void, Void, Exception> function = throwingFunction($ -> {
            throw exception;
        });
        shouldThrow(() -> function.apply(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromFunction() throws Exception {
        final ThrowingFunction<Void, Void, Exception> function = throwingFunction((Void $) -> null);
        shouldNotThrow(() -> function.apply(null));
    }

    @Test
    public void shouldRethrowExceptionFromUnaryOperator() {
        final ThrowingUnaryOperator<Void, Exception> operator = throwingUnaryOperator($ -> {
            throw exception;
        });
        shouldThrow(() -> operator.apply(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromUnaryOperator() throws Exception {
        final ThrowingUnaryOperator<Void, Exception> operator = throwingUnaryOperator((Void $) -> null);
        shouldNotThrow(() -> operator.apply(null));
    }

    @Test
    public void shouldRethrowExceptionFromPredicate() {
        final ThrowingPredicate<Void, Exception> predicate = throwingPredicate($ -> {
            throw exception;
        });
        shouldThrow(() -> predicate.test(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromPredicate() throws Exception {
        final ThrowingPredicate<Void, Exception> predicate = throwingPredicate((Void $) -> false);
        shouldNotThrow(() -> predicate.test(null));
    }

    @Test
    public void shouldRethrowExceptionFromBiConsumer() {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = throwingBiConsumer(($, €) -> {
            throw exception;
        });
        shouldThrow(() -> consumer.accept(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiConsumer() throws Exception {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = throwingBiConsumer(($, €) -> {
        });
        shouldNotThrow(() -> consumer.accept(null, null));
    }

    @Test
    public void shouldRethrowExceptionFromBiFunction() {
        final ThrowingBiFunction<Void, Void, Void, Exception> function = throwingBiFunction(($, €) -> {
            throw exception;
        });
        shouldThrow(() -> function.apply(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiFunction() throws Exception {
        final ThrowingBiFunction<Void, Void, Void, Exception> function = throwingBiFunction(($, €) -> null);
        shouldNotThrow(() -> function.apply(null, null));
    }

    @Test
    public void shouldRethrowExceptionFromBinaryOperator() {
        final ThrowingBinaryOperator<Void, Exception> operator = throwingBinaryOperator(($, €) -> {
            throw exception;
        });
        shouldThrow(() -> operator.apply(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBinaryOperator() throws Exception {
        final ThrowingBinaryOperator<Void, Exception> operator = throwingBinaryOperator(($, €) -> null);
        shouldNotThrow(() -> operator.apply(null, null));
    }

    @Test
    public void shouldRethrowExceptionFromBiPredicate() {
        final ThrowingBiPredicate<Void, Void, Exception> predicate = throwingBiPredicate(($, €) -> {
            throw exception;
        });
        shouldThrow(() -> predicate.test(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiPredicate() throws Exception {
        final ThrowingBiPredicate<Void, Void, Exception> predicate = throwingBiPredicate(($, €) -> false);
        shouldNotThrow(() -> predicate.test(null, null));
    }

    private void shouldNotThrow(final Runnable nonThrower) {
        nonThrower.run();
    }

    private void shouldThrow(final Runnable thrower) {
        expectThrows(Exception.class, thrower::run);
        assertThat(exception, is(sameInstance(exception)));
    }

}