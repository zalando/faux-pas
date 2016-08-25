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
import static org.zalando.fauxpas.FauxPas.throwingConsumer;
import static org.zalando.fauxpas.FauxPas.throwingFunction;
import static org.zalando.fauxpas.FauxPas.throwingPredicate;
import static org.zalando.fauxpas.FauxPas.throwingRunnable;
import static org.zalando.fauxpas.FauxPas.throwingSupplier;

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
        final ThrowingFunction<Void, Void, Exception> consumer = throwingFunction($ -> {
            throw exception;
        });
        shouldThrow(() -> consumer.apply(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromFunction() throws Exception {
        final ThrowingFunction<Void, Void, Exception> consumer = throwingFunction((Void $) -> null);
        shouldNotThrow(() -> consumer.apply(null));
    }

    @Test
    public void shouldRethrowExceptionFromPredicate() {
        final ThrowingPredicate<Void, Exception> consumer = throwingPredicate($ -> {
            throw exception;
        });
        shouldThrow(() -> consumer.test(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromPredicate() throws Exception {
        final ThrowingPredicate<Void, Exception> consumer = throwingPredicate((Void $) -> false);
        shouldNotThrow(() -> consumer.test(null));
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
        final ThrowingBiFunction<Void, Void, Void, Exception> consumer = throwingBiFunction(($, €) -> {
            throw exception;
        });
        shouldThrow(() -> consumer.apply(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiFunction() throws Exception {
        final ThrowingBiFunction<Void, Void, Void, Exception> consumer = throwingBiFunction(($, €) -> null);
        shouldNotThrow(() -> consumer.apply(null, null));
    }

    @Test
    public void shouldRethrowExceptionFromBiPredicate() {
        final ThrowingBiPredicate<Void, Void, Exception> consumer = throwingBiPredicate(($, €) -> {
            throw exception;
        });
        shouldThrow(() -> consumer.test(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiPredicate() throws Exception {
        final ThrowingBiPredicate<Void, Void, Exception> consumer = throwingBiPredicate(($, €) -> false);
        shouldNotThrow(() -> consumer.test(null, null));
    }

    private void shouldNotThrow(final Runnable nonThrower) throws Exception {
        nonThrower.run();
    }

    private void shouldThrow(final Runnable thrower) {
        expectThrows(Exception.class, thrower::run);
        assertThat(exception, is(sameInstance(exception)));
    }

}