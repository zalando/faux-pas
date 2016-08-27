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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.TryWith.tryWith;

@RunWith(JUnitPlatform.class)
public final class TryWithBiFunctionTest {

    private final Object value = new Object();

    private final Closeable outer = mock(Closeable.class);
    private final Closeable inner = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingBiFunction<Closeable, Closeable, ?, Exception> function = mock(ThrowingBiFunction.class);

    @Test
    public void shouldReturnWithoutException() throws Exception {
        doReturn(value).when(function).tryApply(any(), any());

        final Object actual = run();
        assertThat(actual, is(sameInstance(value)));
    }

    @Test
    public void shouldPassResources() throws Exception {
        run();
        verify(function).tryApply(outer, inner);
    }

    @Test
    public void shouldNotFailOnNullResource() throws Exception {
        tryWith(null, null, function);
        verify(function).tryApply(null, null);
    }

    @Test
    public void shouldNotFailOnNullResourceWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any(), any());
        expectThrows(Exception.class, () -> tryWith(null, null, function));
    }

    @Test
    public void shouldCloseWithoutException() throws Exception {
        run();
        verify(inner).close();
        verify(outer).close();
    }

    @Test
    public void shouldThrowException() throws Exception {
        final Exception exception = new Exception();
        doThrow(exception).when(function).tryApply(any(), any());
        final Exception e = expectThrows(Exception.class, this::run);
        assertThat(e, is(sameInstance(exception)));
    }

    @Test
    public void shouldCloseWithException() throws Exception {
        doThrow(new Exception()).when(function).tryApply(any(), any());
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

        doThrow(exception).when(function).tryApply(any(), any());
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

        doThrow(exception).when(function).tryApply(any(), any());
        doThrow(ioException).when(inner).close();
        doThrow(secondIOException).when(outer).close();

        final Exception e = expectThrows(Exception.class, this::run);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), arrayContaining(asList(
                sameInstance(ioException), sameInstance(secondIOException))));
    }

    public Object run() throws Exception {
        return tryWith(outer, inner, function);
    }

}
