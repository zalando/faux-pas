package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

final class TryWithBiConsumerTest {

    private final Closeable outer = mock(Closeable.class);
    private final Closeable inner = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingBiConsumer<Closeable, Closeable, Exception> consumer = mock(ThrowingBiConsumer.class);

    @Test
    void shouldPassResources() throws Exception {
        run();
        verify(consumer).tryAccept(outer, inner);
    }

    @Test
    void shouldCloseWithoutException() throws Exception {
        run();
        verify(inner).close();
        verify(outer).close();
    }

    @Test
    void shouldNotFailOnNullResource() throws Exception {
        tryWith(null, null, consumer);
        verify(consumer).tryAccept(null, null);
    }

    @Test
    void shouldNotFailOnNullResourceWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any(), any());
        assertThrows(Exception.class, () -> tryWith(null, null, consumer));
    }

    @Test
    void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(consumer).tryAccept(any(), any());
        final Exception e = assertThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
    }

    @Test
    void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any(), any());
        assertThrows(Exception.class, this::run);
        verify(outer).close();
        verify(inner).close();
    }

    @Test
    void shouldFailToCloseOuter() throws Exception {
        shouldFailToClose(outer);
    }

    @Test
    void shouldFailToCloseInner() throws Exception {
        shouldFailToClose(inner);
    }

    private void shouldFailToClose(final Closeable resource) throws IOException {
        final IOException ioException = new IOException();
        doThrow(ioException).when(resource).close();
        final IOException e = assertThrows(IOException.class, this::run);
        assertThat(e, is(sameInstance(ioException)));
    }

    @Test
    void shouldFailToCloseOuterWithException() throws Exception {
        shouldFailToCloseWithException(outer);
    }

    @Test
    void shouldFailToCloseInnerWithException() throws Exception {
        shouldFailToCloseWithException(inner);
    }

    private void shouldFailToCloseWithException(final Closeable resource) throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();

        doThrow(exception).when(consumer).tryAccept(any(), any());
        doThrow(ioException).when(resource).close();

        final Exception e = assertThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    @Test
    void shouldFailToCloseOuterAndInnerWithException() throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();
        final IOException secondIOException = new IOException();

        doThrow(exception).when(consumer).tryAccept(any(), any());
        doThrow(ioException).when(inner).close();
        doThrow(secondIOException).when(outer).close();

        final Exception e = assertThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), arrayContaining(asList(
                sameInstance(ioException), sameInstance(secondIOException))));
    }

    private void run() throws Exception {
        tryWith(outer, inner, consumer);
    }

}
