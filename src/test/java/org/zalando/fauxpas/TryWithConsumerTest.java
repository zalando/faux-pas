package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

@RunWith(JUnitPlatform.class)
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
        final Exception e = assertThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
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
        final IOException e = assertThrows(IOException.class, this::run);
        assertThat(e, is(sameInstance(ioException)));
    }

    @Test
    void shouldFailToCloseWithException() throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();
        doThrow(exception).when(consumer).tryAccept(any());
        doThrow(ioException).when(resource).close();

        final Exception e = assertThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    private void run() throws Exception {
        tryWith(resource, consumer);
    }

}
