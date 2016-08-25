package org.zalando.fauxpas;

import org.junit.jupiter.api.Executable;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(JUnitPlatform.class)
public final class LoggingStrategyTest {

    private final Logger logger = mock(Logger.class);
    private final Strategy unit = Strategies.logging(logger);

    @SuppressWarnings("ThrowableInstanceNeverThrown") // we're in fact throwing it, multiple times even...
    private final Exception exception = new Exception();

    @Test
    public void shouldLogExceptionFromRunnable() {
        final ThrowingRunnable<Exception> runnable = () -> {throw exception;};
        shouldThrow(runnable::tryRun, runnable.with(unit));
    }

    @Test
    public void shouldNotLogExceptionFromRunnable() throws Exception {
        final ThrowingRunnable<Exception> runnable = () -> {};
        shouldNotThrow(runnable::tryRun, runnable.with(unit));
    }

    @Test
    public void shouldLogExceptionFromSupplier() {
        final ThrowingSupplier<Void, Exception> supplier = () -> {throw exception;};
        shouldThrow(supplier::tryGet, supplier.with(unit)::get);
    }

    @Test
    public void shouldNotLogExceptionFromSupplier() throws Exception {
        final ThrowingSupplier<Void, Exception> supplier = () -> null;
        shouldNotThrow(supplier::tryGet, supplier.with(unit)::get);
    }

    @Test
    public void shouldLogExceptionFromConsumer() {
        final ThrowingConsumer<Void, Exception> consumer = $ -> {throw exception;};
        shouldThrow(() -> consumer.tryAccept(null), () -> consumer.with(unit).accept(null));
    }

    @Test
    public void shouldNotLogExceptionFromConsumer() throws Exception {
        final ThrowingConsumer<Void, Exception> consumer = $ -> {};
        shouldNotThrow(() -> consumer.tryAccept(null), () -> consumer.with(unit).accept(null));
    }

    @Test
    public void shouldLogExceptionFromFunction() {
        final ThrowingFunction<Void, Void, Exception> consumer = $ -> {throw exception;};
        shouldThrow(() -> consumer.tryApply(null), () -> consumer.with(unit).apply(null));
    }

    @Test
    public void shouldNotLogExceptionFromFunction() throws Exception {
        final ThrowingFunction<Void, Void, Exception> consumer = $ -> null;
        shouldNotThrow(() -> consumer.tryApply(null), () -> consumer.with(unit).apply(null));
    }

    @Test
    public void shouldLogExceptionFromPredicate() {
        final ThrowingPredicate<Void, Exception> consumer = $ -> {throw exception;};
        shouldThrow(() -> consumer.tryTest(null), () -> consumer.with(unit).test(null));
    }

    @Test
    public void shouldNotLogExceptionFromPredicate() throws Exception {
        final ThrowingPredicate<Void, Exception> consumer = $ -> false;
        shouldNotThrow(() -> consumer.tryTest(null), () -> consumer.with(unit).test(null));
    }

    @Test
    public void shouldLogExceptionFromBiConsumer() {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = ($, €) -> {throw exception;};
        shouldThrow(() -> consumer.tryAccept(null, null), () -> consumer.with(unit).accept(null, null));
    }

    @Test
    public void shouldNotLogExceptionFromBiConsumer() throws Exception {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = ($, €) -> {};
        shouldNotThrow(() -> consumer.tryAccept(null, null), () -> consumer.with(unit).accept(null, null));
    }

    @Test
    public void shouldLogExceptionFromBiFunction() {
        final ThrowingBiFunction<Void, Void, Void, Exception> consumer = ($, €) -> {throw exception;};
        shouldThrow(() -> consumer.tryApply(null, null), () -> consumer.with(unit).apply(null, null));
    }

    @Test
    public void shouldNotLogExceptionFromBiFunction() throws Exception {
        final ThrowingBiFunction<Void, Void, Void, Exception> consumer = ($, €) -> null;
        shouldNotThrow(() -> consumer.tryApply(null, null), () -> consumer.with(unit).apply(null, null));
    }

    @Test
    public void shouldLogExceptionFromBiPredicate() {
        final ThrowingBiPredicate<Void, Void, Exception> consumer = ($, €) -> {throw exception;};
        shouldThrow(() -> consumer.tryTest(null, null), () -> consumer.with(unit).test(null, null));
    }

    @Test
    public void shouldNotLogExceptionFromBiPredicate() throws Exception {
        final ThrowingBiPredicate<Void, Void, Exception> consumer = ($, €) -> false;
        shouldNotThrow(() -> consumer.tryTest(null, null), () -> consumer.with(unit).test(null, null));
    }

    private void shouldNotThrow(final ThrowingRunnable<Exception> possibleThrower, final Runnable nonThrower) throws Exception {
        possibleThrower.tryRun();
        nonThrower.run();

        verifyZeroInteractions(logger);
    }

    private void shouldThrow(final Executable thrower, final Runnable nonThrower) {
        expectThrows(Exception.class, thrower);
        assertThat(exception, is(sameInstance(exception)));

        verifyZeroInteractions(logger);

        nonThrower.run();

        verify(logger).error(anyString(), anyObject(), same(exception));
    }

}