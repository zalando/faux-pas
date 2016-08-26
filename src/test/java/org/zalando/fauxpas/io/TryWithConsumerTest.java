package org.zalando.fauxpas.io;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.zalando.fauxpas.ThrowingConsumer;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.io.TryWith.tryWith;

@RunWith(JUnitPlatform.class)
public final class TryWithConsumerTest {

    private final Closeable resource = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingConsumer<Closeable, Exception> consumer = mock(ThrowingConsumer.class);

    @Test
    public void shouldPassResource() throws Exception {
        run();
        verify(consumer).tryAccept(resource);
    }

    @Test
    public void shouldCloseWithoutException() throws Exception {
        run();
        verify(resource).close();
    }

    @Test
    public void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(consumer).tryAccept(any());
        final Exception e = expectThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
    }

    @Test
    public void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any());
        expectThrows(Exception.class, this::run);
        verify(resource).close();
    }

    @Test
    public void shouldFailToClose() throws Exception {
        final IOException ioException = new IOException();
        doThrow(ioException).when(resource).close();
        final IOException e = expectThrows(IOException.class, this::run);
        assertThat(e, is(sameInstance(ioException)));
    }

    @Test
    public void shouldFailToCloseWithException() throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();
        doThrow(exception).when(consumer).tryAccept(any());
        doThrow(ioException).when(resource).close();

        final Exception e = expectThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    public void run() throws Exception {
        tryWith(resource, consumer);
    }

}
