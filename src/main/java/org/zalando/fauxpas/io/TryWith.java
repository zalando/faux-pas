package org.zalando.fauxpas.io;

import org.zalando.fauxpas.ThrowingConsumer;
import org.zalando.fauxpas.ThrowingFunction;

public interface TryWith {

    static <R extends AutoCloseable, X extends Throwable> void tryWith(final R resource,
            final ThrowingConsumer<R, X> consumer) throws X {
        try {
            consumer.tryAccept(resource);
        } catch (final Throwable e) {
            throw tryClose(resource, TryWith.<X>cast(e));
        }

        tryClose(resource);
    }

    static <R extends AutoCloseable, T, X extends Throwable> T tryWith(final R resource,
            final ThrowingFunction<R, T, X> supplier) throws X {

        final T value;

        try {
            value = supplier.tryApply(resource);
        } catch (final Exception e) {
            throw tryClose(resource, TryWith.<X>cast(e));
        }

        tryClose(resource);

        return value;
    }

    static <X extends Throwable> void tryClose(final AutoCloseable resource) throws X {
        try {
            resource.close();
        } catch (final Exception e) {
            // TODO it's actually unsafe, because AutoCloseable could throw anything
            throw TryWith.<X>cast(e);
        }
    }

    static <X extends Throwable> X tryClose(final AutoCloseable closeable, final X e) {
        try {
            closeable.close();
        } catch (final Exception inner) {
            e.addSuppressed(inner);
        }

        return e;
    }

    @SuppressWarnings("unchecked")
    static <X extends Throwable> X cast(final Throwable e) {
        return (X) e;
    }

}
