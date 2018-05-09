package org.zalando.fauxpas;

import org.apiguardian.api.API;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class FauxPas {

    private FauxPas() {

    }

    public static <X extends Throwable> ThrowingRunnable<X> throwingRunnable(
            final ThrowingRunnable<X> runnable) {
        return runnable;
    }

    public static <T, X extends Throwable> ThrowingSupplier<T, X> throwingSupplier(
            final ThrowingSupplier<T, X> supplier) {
        return supplier;
    }

    public static <T, X extends Throwable> ThrowingConsumer<T, X> throwingConsumer(
            final ThrowingConsumer<T, X> consumer) {
        return consumer;
    }

    public static <T, R, X extends Throwable> ThrowingFunction<T, R, X> throwingFunction(
            final ThrowingFunction<T, R, X> function) {
        return function;
    }

    public static <T, X extends Throwable> ThrowingUnaryOperator<T, X> throwingUnaryOperator(
            final ThrowingUnaryOperator<T, X> operator) {
        return operator;
    }

    public static <T, X extends Throwable> ThrowingPredicate<T, X> throwingPredicate(
            final ThrowingPredicate<T, X> predicate) {
        return predicate;
    }

    public static <T, R, X extends Throwable> ThrowingBiConsumer<T, R, X> throwingBiConsumer(
            final ThrowingBiConsumer<T, R, X> consumer) {
        return consumer;
    }

    public static <T, U, R, X extends Throwable> ThrowingBiFunction<T, U, R, X> throwingBiFunction(
            final ThrowingBiFunction<T, U, R, X> function) {
        return function;
    }

    public static <T, X extends Throwable> ThrowingBinaryOperator<T, X> throwingBinaryOperator(
            final ThrowingBinaryOperator<T, X> operator) {
        return operator;
    }

    public static <T, U, X extends Throwable> ThrowingBiPredicate<T, U, X> throwingBiPredicate(
            final ThrowingBiPredicate<T, U, X> predicate) {
        return predicate;
    }

    @API(status = MAINTAINED)
    public static <T extends Throwable, R> Function<Throwable, R> partially(final Class<T> type,
            final ThrowingFunction<T, R, Throwable> function) {
        return partially(e -> {
            if (type.isInstance(e)) {
                return function.apply(type.cast(e));
            }
            throw e;
        });
    }

    @API(status = MAINTAINED)
    public static <R> Function<Throwable, R> partially(final ThrowingFunction<Throwable, R, Throwable> function) {
        return throwable -> {
            try {
                return function.tryApply(unpack(throwable));
            } catch (final CompletionException e) {
                throw e;
            } catch (final Throwable e) {
                throw new CompletionException(e);
            }
        };
    }

    @API(status = EXPERIMENTAL)
    public static <R, T extends Throwable> ThrowingBiConsumer<R, Throwable, Throwable> failedWith(
            final Class<T> type, final ThrowingConsumer<? super T, Throwable> action) {
        return (result, throwable) -> {
            if (nonNull(throwable)) {
                final Throwable unpacked = unpack(throwable);
                if (type.isInstance(unpacked)) {
                    action.tryAccept(type.cast(unpacked));
                }
            }
        };
    }

    private static Throwable unpack(final Throwable throwable) {
        final Throwable cause = throwable.getCause();
        return throwable instanceof CompletionException && cause != null ? cause : throwable;
    }

    @API(status = EXPERIMENTAL)
    public static <T> CompletableFuture<T> handleCompose(final CompletableFuture<T> future,
            final BiFunction<T, Throwable, CompletableFuture<T>> function) {
        return future
                .handle(function)
                .thenCompose(identity());
    }

    @API(status = EXPERIMENTAL)
    public static <T> CompletableFuture<T> exceptionallyCompose(final CompletableFuture<T> future,
            final Function<Throwable, CompletableFuture<T>> function) {
        return future
                .thenApply(CompletableFuture::completedFuture)
                .exceptionally(function)
                .thenCompose(identity());
    }

}
