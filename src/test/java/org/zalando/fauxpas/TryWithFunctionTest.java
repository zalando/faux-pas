package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.zalando.fauxpas.ThrowingFunction;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

@RunWith(JUnitPlatform.class)
public final class TryWithFunctionTest {

    private final Object value = new Object();

    private final Closeable resource = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingFunction<Closeable, ?, Exception> function = mock(ThrowingFunction.class);

    @Test
    public void shouldPassResource() throws Exception {
        run();
        verify(function).tryApply(resource);
    }

    @Test
    public void shouldReturnWithoutException() throws Exception {
        doReturn(value).when(function).tryApply(any());

        final Object actual = run();
        assertThat(actual, is(sameInstance(value)));
    }

    @Test
    public void shouldCloseWithoutException() throws Exception {
        run();
        verify(resource).close();
    }

    @Test
    public void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(function).tryApply(any());
        final Exception e = expectThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
    }

    @Test
    public void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any());
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
        doThrow(exception).when(function).tryApply(any());
        doThrow(ioException).when(resource).close();

        final Exception e = expectThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    public Object run() throws Exception {
        return tryWith(resource, function);
    }

}
