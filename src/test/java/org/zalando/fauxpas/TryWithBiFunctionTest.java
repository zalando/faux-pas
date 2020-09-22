package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

final class TryWithBiFunctionTest {

    private final Object value = new Object();

    private final Closeable outer = mock(Closeable.class);
    private final Closeable inner = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingBiFunction<Closeable, Closeable, ?, Exception> function = mock(ThrowingBiFunction.class);

    @Test
    void shouldReturnWithoutException() throws Exception {
        doReturn(value).when(function).tryApply(any(), any());

        final Object actual = run();
        assertThat(actual).isSameAs(value);
    }

    @Test
    void shouldPassResources() throws Exception {
        run();
        verify(function).tryApply(outer, inner);
    }

    @Test
    void shouldNotFailOnNullResource() throws Exception {
        tryWith(null, null, function);
        verify(function).tryApply(null, null);
    }

    @Test
    void shouldNotFailOnNullResourceWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any(), any());
        assertThrows(Exception.class, () -> tryWith(null, null, function));
    }

    @Test
    void shouldCloseWithoutException() throws Exception {
        run();
        verify(inner).close();
        verify(outer).close();
    }

    @Test
    void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(function).tryApply(any(), any());
        final Exception thrown = assertThrows(Exception.class, this::run);
        assertThat(thrown).isSameAs(exception);
    }

    @Test
    void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any(), any());
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
        final IOException thrown = assertThrows(IOException.class, this::run);
        assertThat(thrown).isSameAs(ioException);
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

        doThrow(exception).when(function).tryApply(any(), any());
        doThrow(ioException).when(resource).close();

        final Exception thrown = assertThrows(Exception.class, this::run);

        assertThat(thrown)
                .isSameAs(exception)
                .hasSuppressedException(ioException);
    }

    @Test
    void shouldFailToCloseOuterAndInnerWithException() throws Exception {
        final Exception exception = new Exception();
        final IOException ioException = new IOException();
        final IOException secondIOException = new IOException();

        doThrow(exception).when(function).tryApply(any(), any());
        doThrow(ioException).when(inner).close();
        doThrow(secondIOException).when(outer).close();

        final Exception thrown = assertThrows(Exception.class, this::run);

        assertThat(thrown)
                .isSameAs(exception)
                .hasSuppressedException(ioException)
                .hasSuppressedException(secondIOException);
    }

    private Object run() throws Exception {
        return tryWith(outer, inner, function);
    }

}
