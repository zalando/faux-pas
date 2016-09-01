package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.Closeable;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

@RunWith(JUnitPlatform.class)
public final class TryWithBiConsumerTest {

    private final Closeable outer = mock(Closeable.class);
    private final Closeable inner = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingBiConsumer<Closeable, Closeable, Exception> consumer = mock(ThrowingBiConsumer.class);

    @Test
    public void shouldPassResources() throws Exception {
        run();
        verify(consumer).tryAccept(outer, inner);
    }

    @Test
    public void shouldCloseWithoutException() throws Exception {
        run();
        verify(inner).close();
        verify(outer).close();
    }

    @Test
    public void shouldNotFailOnNullResource() throws Exception {
        tryWith(null, null, consumer);
        verify(consumer).tryAccept(null, null);
    }

    @Test
    public void shouldNotFailOnNullResourceWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any(), any());
        expectThrows(Exception.class, () -> tryWith(null, null, consumer));
    }

    @Test
    public void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(consumer).tryAccept(any(), any());
        final Exception e = expectThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
    }

    @Test
    public void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(consumer).tryAccept(any(), any());
        expectThrows(Exception.class, this::run);
        verify(outer).close();
        verify(inner).close();
    }

    @Test
    public void shouldFailToCloseOuter() throws Exception {
        shouldFailToClose(outer);
    }

    @Test
    public void shouldFailToCloseInner() throws Exception {
        shouldFailToClose(inner);
    }

    public void shouldFailToClose(final Closeable resource) throws IOException {
        final IOException ioException = new IOException();
        doThrow(ioException).when(resource).close();
        final IOException e = expectThrows(IOException.class, this::run);
        assertThat(e, is(sameInstance(ioException)));
    }

    @Test
    public void shouldFailToCloseOuterWithException() throws Exception {
        shouldFailToCloseWithException(outer);
    }

    @Test
    public void shouldFailToCloseInnerWithException() throws Exception {
        shouldFailToCloseWithException(inner);
    }

    public void shouldFailToCloseWithException(final Closeable resource) throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();

        doThrow(exception).when(consumer).tryAccept(any(), any());
        doThrow(ioException).when(resource).close();

        final Exception e = expectThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    @Test
    public void shouldFailToCloseOuterAndInnerWithException() throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();
        final IOException secondIOException = new IOException();

        doThrow(exception).when(consumer).tryAccept(any(), any());
        doThrow(ioException).when(inner).close();
        doThrow(secondIOException).when(outer).close();

        final Exception e = expectThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), arrayContaining(asList(
                sameInstance(ioException), sameInstance(secondIOException))));
    }

    public void run() throws Exception {
        tryWith(outer, inner, consumer);
    }

}
