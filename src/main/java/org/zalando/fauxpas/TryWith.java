package org.zalando.fauxpas;

import lombok.SneakyThrows;

public final class TryWith {

    TryWith() {
        // package private so we can trick code coverage
    }

    public static <O extends AutoCloseable, I extends AutoCloseable, X extends Throwable> void tryWith(final O outer,
            final I inner, final ThrowingBiConsumer<O, I, X> consumer) throws X {

        tryWith(outer, (ಠ_ಠ) -> {
            tryWith(inner, (ツ) -> {
                consumer.tryAccept(outer, inner);
            });
        });
    }

    public static <R extends AutoCloseable, X extends Throwable> void tryWith(final R resource,
            final ThrowingConsumer<R, X> consumer) throws X {

        try {
            consumer.tryAccept(resource);
        } catch (final Throwable e) {
            throw tryClose(resource, e);
        }

        tryClose(resource);
    }

    public static <O extends AutoCloseable, I extends AutoCloseable, T, X extends Throwable> T tryWith(final O outer,
            final I inner, final ThrowingBiFunction<O, I, T, X> function) throws X {

        // not exactly sure why those explicit type parameters are needed
        return TryWith.<O, T, X>tryWith(outer, (ಠ_ಠ) -> {
            return tryWith(inner, (ツ) -> {
                return function.tryApply(outer, inner);
            });
        });
    }

    public static <R extends AutoCloseable, T, X extends Throwable> T tryWith(final R resource,
            final ThrowingFunction<R, T, X> supplier) throws X {

        final T value;

        try {
            value = supplier.tryApply(resource);
        } catch (final Exception e) {
            throw tryClose(resource, e);
        }

        tryClose(resource);

        return value;
    }

    @SneakyThrows
    private static void tryClose(final AutoCloseable resource) {
        resource.close();
    }

    @SneakyThrows
    private static RuntimeException tryClose(final AutoCloseable closeable, final Throwable e) {
        try {
            closeable.close();
        } catch (final Exception inner) {
            e.addSuppressed(inner);
        }

        throw e;
    }

}
