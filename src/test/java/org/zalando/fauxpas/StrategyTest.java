package org.zalando.fauxpas;

import org.junit.jupiter.api.Executable;
import org.junit.jupiter.api.Test;

import static org.zalando.fauxpas.FauxPas.throwingBiConsumer;
import static org.zalando.fauxpas.FauxPas.throwingBiFunction;
import static org.zalando.fauxpas.FauxPas.throwingBiPredicate;
import static org.zalando.fauxpas.FauxPas.throwingConsumer;
import static org.zalando.fauxpas.FauxPas.throwingFunction;
import static org.zalando.fauxpas.FauxPas.throwingPredicate;
import static org.zalando.fauxpas.FauxPas.throwingRunnable;
import static org.zalando.fauxpas.FauxPas.throwingSupplier;

public abstract class StrategyTest {

    @SuppressWarnings("ThrowableInstanceNeverThrown") // we're in fact throwing it, multiple times even...
    private final Exception exception = new Exception();
    
    protected abstract Strategy unit();
    
    @Test
    public void shouldHandleExceptionFromRunnable() throws Throwable {
        final ThrowingRunnable<Exception> runnable = () -> {throw exception;};
        testOriginalWithException(exception, runnable::tryRun);
        testAdaptedWithException(exception, throwingRunnable(runnable, unit())::run);
    }

    @Test
    public void shouldNotHandleExceptionFromRunnable() throws Throwable {
        final ThrowingRunnable<Exception> runnable = () -> {};
        testOriginalWithoutException(runnable::tryRun);
        testAdaptedWithoutException(throwingRunnable(runnable, unit())::run);
    }

    @Test
    public void shouldHandleExceptionFromSupplier() throws Throwable {
        final ThrowingSupplier<Void, Exception> supplier = () -> {throw exception;};
        testOriginalWithException(exception, supplier::tryGet);
        testAdaptedWithException(exception, throwingSupplier(supplier, unit())::get);
    }

    @Test
    public void shouldNotHandleExceptionFromSupplier() throws Throwable {
        final ThrowingSupplier<Void, Exception> supplier = () -> null;
        testOriginalWithoutException(supplier::tryGet);
        testAdaptedWithoutException(throwingSupplier(supplier, unit())::get);
    }

    @Test
    public void shouldHandleExceptionFromConsumer() throws Throwable {
        final ThrowingConsumer<Void, Exception> consumer = $ -> {throw exception;};
        testOriginalWithException(exception, () -> consumer.tryAccept(null));
        testAdaptedWithException(exception, () -> throwingConsumer(consumer, unit()).accept(null));
    }

    @Test
    public void shouldNotHandleExceptionFromConsumer() throws Throwable {
        final ThrowingConsumer<Void, Exception> consumer = $ -> {};
        testOriginalWithoutException(() -> consumer.tryAccept(null));
        testAdaptedWithoutException(() -> throwingConsumer(consumer, unit()).accept(null));
    }

    @Test
    public void shouldHandleExceptionFromFunction() throws Throwable {
        final ThrowingFunction<Void, Void, Exception> function = $ -> {throw exception;};
        testOriginalWithException(exception, () -> function.tryApply(null));
        testAdaptedWithException(exception, () -> throwingFunction(function, unit()).apply(null));
    }

    @Test
    public void shouldNotHandleExceptionFromFunction() throws Throwable {
        final ThrowingFunction<Void, Void, Exception> function = $ -> null;
        testOriginalWithoutException(() -> function.tryApply(null));
        testAdaptedWithoutException(() -> throwingFunction(function, unit()).apply(null));
    }

    @Test
    public void shouldHandleExceptionFromPredicate() throws Throwable {
        final ThrowingPredicate<Void, Exception> predicate = $ -> {throw exception;};
        testOriginalWithException(exception, () -> predicate.tryTest(null));
        testAdaptedWithException(exception, () -> throwingPredicate(predicate, unit()).test(null));
    }

    @Test
    public void shouldNotHandleExceptionFromPredicate() throws Throwable {
        final ThrowingPredicate<Void, Exception> predicate = $ -> false;
        testOriginalWithoutException(() -> predicate.tryTest(null));
        testAdaptedWithoutException(() -> throwingPredicate(predicate, unit()).test(null));
    }

    @Test
    public void shouldHandleExceptionFromBiConsumer() throws Throwable {
        final ThrowingBiConsumer<Void, Void, Exception> biConsumer = (x, y) -> {throw exception;};
        testOriginalWithException(exception, () -> biConsumer.tryAccept(null, null));
        testAdaptedWithException(exception, () -> throwingBiConsumer(biConsumer, unit()).accept(null, null));
    }

    @Test
    public void shouldNotHandleExceptionFromBiConsumer() throws Throwable {
        final ThrowingBiConsumer<Void, Void, Exception> biConsumer = (x, y) -> {};
        testOriginalWithoutException(() -> biConsumer.tryAccept(null, null));
        testAdaptedWithoutException(() -> throwingBiConsumer(biConsumer, unit()).accept(null, null));
    }

    @Test
    public void shouldHandleExceptionFromBiFunction() throws Throwable {
        final ThrowingBiFunction<Void, Void, Void, Exception> biFunction = (x, y) -> {throw exception;};
        testOriginalWithException(exception, () -> biFunction.tryApply(null, null));
        testAdaptedWithException(exception, () -> throwingBiFunction(biFunction, unit()).apply(null, null));
    }

    @Test
    public void shouldNotHandleExceptionFromBiFunction() throws Throwable {
        final ThrowingBiFunction<Void, Void, Void, Exception> biFunction = (x, y) -> null;
        testOriginalWithoutException(() -> biFunction.tryApply(null, null));
        testAdaptedWithoutException(() -> throwingBiFunction(biFunction, unit()).apply(null, null));
    }

    @Test
    public void shouldHandleExceptionFromBiPredicate() throws Throwable {
        final ThrowingBiPredicate<Void, Void, Exception> biPredicate = (x, y) -> {throw exception;};
        testOriginalWithException(exception, () -> biPredicate.tryTest(null, null));
        testAdaptedWithException(exception, () ->  throwingBiPredicate(biPredicate, unit()).test(null, null));
    }

    @Test
    public void shouldNotHandleExceptionFromBiPredicate() throws Throwable {
        final ThrowingBiPredicate<Void, Void, Exception> biPredicate = (x, y) -> false;
        testOriginalWithoutException(() -> biPredicate.tryTest(null, null));
        testAdaptedWithoutException(() -> throwingBiPredicate(biPredicate, unit()).test(null, null));
    }

    protected abstract void testOriginalWithException(Throwable expected, Executable executable) throws Throwable;
    protected abstract void testAdaptedWithException(Throwable expected, Executable executable) throws Throwable;
    protected abstract void testOriginalWithoutException(Executable executable) throws Throwable;
    protected abstract void testAdaptedWithoutException(Executable executable) throws Throwable;

}
