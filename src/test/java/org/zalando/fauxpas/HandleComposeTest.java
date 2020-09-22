package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.fauxpas.FauxPas.handleCompose;

class HandleComposeTest {

    @Test
    void shouldHandleComposeResult() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = handleCompose(original, (s, throwable) ->
                completedFuture("result"));

        original.complete("foo");

        assertThat(unit).isCompletedWithValue("result");
    }

    @Test
    void shouldHandleComposeException() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        final CompletableFuture<String> unit = handleCompose(original, (s, throwable) ->
            completedFuture("result"));

        original.completeExceptionally(new RuntimeException());

        assertThat(unit).isCompletedWithValue("result");
    }

}
