package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.zalando.fauxpas.FauxPas.failedWith;

@ExtendWith(MockitoExtension.class)
class FailedWithTest {

    @Mock
    private ThrowingConsumer<Throwable, Throwable> action;

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

        verifyNoInteractions(action);
    }

    @Test
    void shouldNotMatchIfExceptionTypeIsDifferent() {
        final CompletableFuture<String> original = new CompletableFuture<>();
        original.whenComplete(failedWith(IllegalStateException.class, action));

        original.completeExceptionally(new IllegalArgumentException());

        verifyNoInteractions(action);
    }

}
