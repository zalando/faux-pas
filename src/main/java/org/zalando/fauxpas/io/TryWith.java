package org.zalando.fauxpas.io;

import org.zalando.fauxpas.ThrowingRunnable;
import org.zalando.fauxpas.ThrowingSupplier;

public interface TryWith {

    static void tryWith(final ThrowingRunnable<Exception> runnable, final AutoCloseable closeable) throws Exception {
        try {
            runnable.tryRun();
        } catch (final Exception e) {
            throw tryClose(closeable, e);
        }

        closeable.close();
    }

    static <T> T tryWith(final ThrowingSupplier<T, Exception> supplier, final AutoCloseable closeable)
            throws Exception {

        final T value;

        try {
            value = supplier.tryGet();
        } catch (final Exception e) {
            throw tryClose(closeable, e);
        }

        closeable.close();
        return value;
    }

    static <X extends Exception> X tryClose(final AutoCloseable closeable, final X e) {
        try {
            closeable.close();
        } catch (final Exception inner) {
            e.addSuppressed(inner);
        }

        return e;
    }

}
