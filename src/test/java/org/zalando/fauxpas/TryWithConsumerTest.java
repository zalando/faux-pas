package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

final class TryWithConsumerTest {

    private final Closeable resource = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingConsumer<Closeable, Exception> consumer = mock(ThrowingConsumer.class);

    @Test
    void shouldPassResource() throws Exception {
        run();
        verify(consumer).tryAccept(resource);
    }

    @Test
    void shouldCloseWithoutException() throws Exception {
        run();
        verify(resource).close();
    }

    @Test
    void shouldNotFailOnNullResource() throws Exception {
        tryWith(null, consumer);
        verify(consumer).tryAccept(null);
    }

    @Test
    void shouldNotFailOnNullResourceWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any());
        assertThrows(Exception.class, () -> tryWith(null, consumer));
    }

    @Test
    void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(consumer).tryAccept(any());
        final Exception thrown = assertThrows(Exception.class, this::run);
        assertThat(thrown).isSameAs(exception);
    }

    @Test
    void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any());
        assertThrows(Exception.class, this::run);
        verify(resource).close();
    }

    @Test
    void shouldFailToClose() throws Exception {
        final IOException ioException = new IOException();
        doThrow(ioException).when(resource).close();
        final IOException thrown = assertThrows(IOException.class, this::run);
        assertThat(thrown).isSameAs(ioException);
    }

    @Test
    void shouldFailToCloseWithException() throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();
        doThrow(exception).when(consumer).tryAccept(any());
        doThrow(ioException).when(resource).close();

        final Exception thrown = assertThrows(Exception.class, this::run);

        assertThat(thrown)
                .isSameAs(exception)
                .hasSuppressedException(ioException);
    }

    private void run() throws Exception {
        tryWith(resource, consumer);
    }

}
