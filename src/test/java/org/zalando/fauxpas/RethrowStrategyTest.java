package org.zalando.fauxpas;

import org.junit.jupiter.api.Executable;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(JUnitPlatform.class)
public final class RethrowStrategyTest {

    @SuppressWarnings("unchecked")
    private final Function<Throwable, RuntimeException> transformer = mock(Function.class);
    
    private final Strategy unit = Strategies.rethrow(transformer);

    @SuppressWarnings("ThrowableInstanceNeverThrown") // we're in fact throwing it, multiple times even...
    private final Exception exception = new Exception();

    @Test
    public void shouldRethrowExceptionFromRunnable() {
        final ThrowingRunnable<Exception> runnable = () -> {throw exception;};
        shouldThrow(runnable::tryRun, runnable.with(unit)::run);
    }

    @Test
    public void shouldNotRethrowExceptionFromRunnable() throws Exception {
        final ThrowingRunnable<Exception> runnable = () -> {};
        shouldNotThrow(runnable::tryRun, runnable.with(unit));
    }

    @Test
    public void shouldRethrowExceptionFromSupplier() {
        final ThrowingSupplier<Void, Exception> supplier = () -> {throw exception;};
        shouldThrow(supplier::tryGet, supplier.with(unit)::get);
    }

    @Test
    public void shouldNotRethrowExceptionFromSupplier() throws Exception {
        final ThrowingSupplier<Void, Exception> supplier = () -> null;
        shouldNotThrow(supplier::tryGet, supplier.with(unit)::get);
    }

    @Test
    public void shouldRethrowExceptionFromConsumer() {
        final ThrowingConsumer<Void, Exception> consumer = $ -> {throw exception;};
        shouldThrow(() -> consumer.tryAccept(null), () -> consumer.with(unit).accept(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromConsumer() throws Exception {
        final ThrowingConsumer<Void, Exception> consumer = $ -> {};
        shouldNotThrow(() -> consumer.tryAccept(null), () -> consumer.with(unit).accept(null));
    }

    @Test
    public void shouldRethrowExceptionFromFunction() {
        final ThrowingFunction<Void, Void, Exception> consumer = $ -> {throw exception;};
        shouldThrow(() -> consumer.tryApply(null), () -> consumer.with(unit).apply(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromFunction() throws Exception {
        final ThrowingFunction<Void, Void, Exception> consumer = $ -> null;
        shouldNotThrow(() -> consumer.tryApply(null), () -> consumer.with(unit).apply(null));
    }

    @Test
    public void shouldRethrowExceptionFromPredicate() {
        final ThrowingPredicate<Void, Exception> consumer = $ -> {throw exception;};
        shouldThrow(() -> consumer.tryTest(null), () -> consumer.with(unit).test(null));
    }

    @Test
    public void shouldNotRethrowExceptionFromPredicate() throws Exception {
        final ThrowingPredicate<Void, Exception> consumer = $ -> false;
        shouldNotThrow(() -> consumer.tryTest(null), () -> consumer.with(unit).test(null));
    }

    @Test
    public void shouldRethrowExceptionFromBiConsumer() {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = ($, €) -> {throw exception;};
        shouldThrow(() -> consumer.tryAccept(null, null), () -> consumer.with(unit).accept(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiConsumer() throws Exception {
        final ThrowingBiConsumer<Void, Void, Exception> consumer = ($, €) -> {};
        shouldNotThrow(() -> consumer.tryAccept(null, null), () -> consumer.with(unit).accept(null, null));
    }

    @Test
    public void shouldRethrowExceptionFromBiFunction() {
        final ThrowingBiFunction<Void, Void, Void, Exception> consumer = ($, €) -> {throw exception;};
        shouldThrow(() -> consumer.tryApply(null, null), () -> consumer.with(unit).apply(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiFunction() throws Exception {
        final ThrowingBiFunction<Void, Void, Void, Exception> consumer = ($, €) -> null;
        shouldNotThrow(() -> consumer.tryApply(null, null), () -> consumer.with(unit).apply(null, null));
    }

    @Test
    public void shouldRethrowExceptionFromBiPredicate() {
        final ThrowingBiPredicate<Void, Void, Exception> consumer = ($, €) -> {throw exception;};
        shouldThrow(() -> consumer.tryTest(null, null), () -> consumer.with(unit).test(null, null));
    }

    @Test
    public void shouldNotRethrowExceptionFromBiPredicate() throws Exception {
        final ThrowingBiPredicate<Void, Void, Exception> consumer = ($, €) -> false;
        shouldNotThrow(() -> consumer.tryTest(null, null), () -> consumer.with(unit).test(null, null));
    }

    private void shouldNotThrow(final ThrowingRunnable<Exception> checkedThrower, final Runnable uncheckedThrower) throws Exception {
        checkedThrower.tryRun();
        uncheckedThrower.run();

        verifyZeroInteractions(transformer);
    }

    private void shouldThrow(final Executable checkedThrower, final Executable uncheckedThrower) {
        final Exception actual = expectThrows(Exception.class, checkedThrower);
        assertThat(actual, is(sameInstance(exception)));
        verifyZeroInteractions(transformer);

        expectThrows(RuntimeException.class, uncheckedThrower);
        verify(transformer).apply(same(exception));
    }

}