package org.zalando.fauxpas.io;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.zalando.fauxpas.ThrowingRunnable;
import org.zalando.fauxpas.ThrowingSupplier;

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.zalando.fauxpas.io.TryWith.tryWith;

@RunWith(JUnitPlatform.class)
public final class TryWithTest {

    private final Object value = new Object();

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private final Exception exception = new Exception();

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private final IOException ioException = new IOException();

    private final Closeable closable = mock(Closeable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingRunnable<Exception> runnable = mock(ThrowingRunnable.class);

    @SuppressWarnings("unchecked")
    private final ThrowingSupplier<?, Exception> supplier = mock(ThrowingSupplier.class);

    @Test
    public void shouldCloseAfterRunningWithoutException() throws Exception {
        final Closeable closable = mock(Closeable.class);

        tryWith(runnable, closable);

        verify(runnable).tryRun();
        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterRunningWithException() throws Exception {
        doThrow(exception).when(runnable).tryRun();

        final Exception e = expectThrows(Exception.class, this::runRunnable);

        assertThat(e, is(sameInstance(exception)));

        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterRunningAndExposeExceptionWhenFailingToClose() throws Exception {
        doThrow(ioException).when(closable).close();

        final IOException e = expectThrows(IOException.class, this::runRunnable);

        assertThat(e, is(sameInstance(ioException)));
    }

    @Test
    public void shouldCloseAfterRunningAndSupressExceptionWhenFailingToClose() throws Exception {
        doThrow(exception).when(runnable).tryRun();
        doThrow(ioException).when(closable).close();

        final Exception e = expectThrows(Exception.class, this::runRunnable);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    public void runRunnable() throws Exception {
        tryWith(runnable, closable);
    }

    @Test
    public void shouldCloseAfterProvidingWithoutException() throws Exception {
        doReturn(value).when(supplier).tryGet();

        final Object actual = runSupplier();
        assertThat(actual, is(sameInstance(value)));

        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterProvidingWithException() throws Exception {
        doThrow(exception).when(supplier).tryGet();

        final Exception e = expectThrows(Exception.class, this::runSupplier);

        assertThat(e, is(sameInstance(exception)));

        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterProvidingAndExposeExceptionWhenFailingToClose() throws Exception {
        doReturn(value).when(supplier).tryGet();
        doThrow(ioException).when(closable).close();

        final Exception e = expectThrows(Exception.class, this::runSupplier);

        assertThat(e, is(sameInstance(ioException)));

        verify(supplier).tryGet();
        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterProvidingAndSupressExceptionWhenFailingToClose() throws Exception {
        doThrow(exception).when(supplier).tryGet();
        doThrow(ioException).when(closable).close();

        final Exception e = expectThrows(Exception.class, this::runSupplier);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    public Object runSupplier() throws Exception {
        return tryWith(supplier, closable);
    }

}
