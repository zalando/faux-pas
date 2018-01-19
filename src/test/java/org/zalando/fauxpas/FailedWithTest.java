package org.zalando.fauxpas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.zalando.fauxpas.FauxPas.failedWith;

class FailedWithTest {

    @Mock
    private ThrowingConsumer<Throwable, Throwable> action;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldMatch() throws Throwable {
        final CompletableFuture<String> original = new CompletableFuture<>();
        original.whenComplete(failedWith(IllegalArgumentException.class, action));

        final IllegalArgumentException e = new IllegalArgumentException();
        original.completeExceptionally(e);

        verify(action).tryAccept(e);
    }

    @Test
    void shouldNotMatchIfCompletedSuccessfully() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        original.whenComplete(failedWith(IllegalStateException.class, action));

        original.complete("result");

        verifyZeroInteractions(action);
    }

    @Test
    void shouldNotMatchIfExceptionTypeIsDifferent() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        original.whenComplete(failedWith(IllegalStateException.class, action));

        original.completeExceptionally(new IllegalArgumentException());

        verifyZeroInteractions(action);
    }

}
