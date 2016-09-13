package org.zalando.fauxpas;

import lombok.SneakyThrows;

import javax.annotation.Nullable;

public final class TryWith {

    TryWith() {
        // package private so we can trick code coverage
    }

    public static <O extends AutoCloseable, I extends AutoCloseable, X extends Throwable> void tryWith(
            @Nullable final O outer, @Nullable final I inner, final ThrowingBiConsumer<O, I, X> consumer) throws X {

        tryWith(outer, (ಠ_ಠ) -> {
            tryWith(inner, (ツ) -> {
                consumer.tryAccept(outer, inner);
            });
        });
    }

    public static <R extends AutoCloseable, X extends Throwable> void tryWith(@Nullable final R resource,
            final ThrowingConsumer<R, X> consumer) throws X {

        try {
            consumer.tryAccept(resource);
        } catch (final Throwable e) {
            throw tryClose(resource, TryWith.<X>cast(e));
        }

        tryClose(resource);
    }

    public static <O extends AutoCloseable, I extends AutoCloseable, T, X extends Throwable> T tryWith(
            @Nullable final O outer, @Nullable final I inner, final ThrowingBiFunction<O, I, T, X> function) throws X {

        // not exactly sure why those explicit type parameters are needed
        return TryWith.<O, T, X>tryWith(outer, (ಠ_ಠ) -> {
            return tryWith(inner, (ツ) -> {
                return function.tryApply(outer, inner);
            });
        });
    }

    public static <R extends AutoCloseable, T, X extends Throwable> T tryWith(@Nullable final R resource,
            final ThrowingFunction<R, T, X> supplier) throws X {

        final T value;

        try {
            value = supplier.tryApply(resource);
        } catch (final Throwable e) {
            throw tryClose(resource, TryWith.<X>cast(e));
        }

        tryClose(resource);

        return value;
    }

    @SneakyThrows
    private static void tryClose(@Nullable final AutoCloseable resource) {
        if (resource == null) {
            return;
        }

        resource.close();
    }

    @SuppressWarnings("unchecked")
    private static <X extends Throwable> X cast(final Throwable e) {
        return (X) e;
    }

    private static <X extends Throwable> X tryClose(@Nullable final AutoCloseable closeable, final X e) {
        if (closeable == null) {
            return e;
        }

        try {
            closeable.close();
        } catch (final Throwable inner) {
            e.addSuppressed(inner);
        }

        return e;
    }

}
