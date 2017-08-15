package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

final class TryWithFunctionTest {

    private final Object value = new Object();

    private final Closeable resource = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingFunction<Closeable, ?, Exception> function = mock(ThrowingFunction.class);

    @Test
    void shouldPassResource() throws Exception {
        run();
        verify(function).tryApply(resource);
    }

    @Test
    void shouldReturnWithoutException() throws Exception {
        doReturn(value).when(function).tryApply(any());

        final Object actual = run();
        assertThat(actual, is(sameInstance(value)));
    }

    @Test
    void shouldCloseWithoutException() throws Exception {
        run();
        verify(resource).close();
    }

    @Test
    void shouldNotFailOnNullResource() throws Exception {
        tryWith(null, function);
        verify(function).tryApply(null);
    }

    @Test
    void shouldNotFailOnNullResourceWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any());
        assertThrows(Exception.class, () -> tryWith(null, function));
    }

    @Test
    void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(function).tryApply(any());
        final Exception e = assertThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
    }

    @Test
    void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any());
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
        doThrow(exception).when(function).tryApply(any());
        doThrow(ioException).when(resource).close();

        final Exception e = assertThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    private Object run() throws Exception {
        return tryWith(resource, function);
    }

}
