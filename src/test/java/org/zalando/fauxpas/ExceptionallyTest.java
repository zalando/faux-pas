package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.fauxpas.FauxPas.partially;

@RunWith(JUnitPlatform.class)
class ExceptionallyTest {

    private String fallback(final Throwable e) throws Throwable {
        if (e instanceof UnsupportedOperationException) {
            return "fallback";
        }
        throw e;
    }

    @Test
    void shouldUseResult() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original.exceptionally(partially(this::fallback));

        original.complete("result");

        assertThat(unit.join(), is("result"));
    }

    @Test
    void shouldUseFallback() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original.exceptionally(partially(this::fallback));

        original.completeExceptionally(new UnsupportedOperationException());

        assertThat(unit.join(), is("fallback"));
    }

    @Test
    void shouldReceiveCompletedException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original.exceptionally(partially(this::fallback));

        original.completeExceptionally(new IOException());

        final CompletionException exception = assertThrows(CompletionException.class, unit::join);
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }

    @Test
    void shouldReceiveUnpackedCompletedException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original.exceptionally(partially(this::fallback));

        original.completeExceptionally(new CompletionException(new IOException()));

        final CompletionException exception = assertThrows(CompletionException.class, unit::join);
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }

    @Test
    void shouldReceiveException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original
                .thenApply(failWith(new IOException()))
                .exceptionally(partially(this::fallback));

        original.complete("result");

        final CompletionException exception = assertThrows(CompletionException.class, unit::join);
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }

    @Test
    void shouldReceiveUnpackedException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original
                .thenApply(failWith(new CompletionException(new IOException())))
                .exceptionally(partially(this::fallback));

        original.complete("result");

        final CompletionException exception = assertThrows(CompletionException.class, unit::join);
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }

    private ThrowingFunction<String, String, Exception> failWith(final Exception e) {
        return result -> {
            throw e;
        };
    }

    @Test
    void shouldThrowPackedException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original.exceptionally(partially(e -> {
            throw e;
        }));

        original.completeExceptionally(new CompletionException(new IOException()));

        final CompletionException exception = assertThrows(CompletionException.class, unit::join);
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }

    @Test
    void shouldThrowSinglePackedException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = original.exceptionally(partially(e -> {
            throw new CompletionException(e);
        }));

        original.completeExceptionally(new CompletionException(new IOException()));

        final CompletionException exception = assertThrows(CompletionException.class, unit::join);
        assertThat(exception.getCause(), is(instanceOf(IOException.class)));
    }

}
