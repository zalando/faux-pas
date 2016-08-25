package org.zalando.fauxpas.io;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.zalando.fauxpas.ThrowingConsumer;
import org.zalando.fauxpas.ThrowingFunction;

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
    private final ThrowingConsumer<Closeable, Exception> consumer = mock(ThrowingConsumer.class);

    @SuppressWarnings("unchecked")
    private final ThrowingFunction<Closeable, ?, Exception> function = mock(ThrowingFunction.class);

    @Test
    public void shouldCloseAfterRunningWithoutException() throws Exception {
        final Closeable closable = mock(Closeable.class);

        tryWith(closable, consumer);

        verify(consumer).tryAccept(closable);
        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterRunningWithException() throws Exception {
        doThrow(exception).when(consumer).tryAccept(closable);

        final Exception e = expectThrows(Exception.class, this::runConsumer);

        assertThat(e, is(sameInstance(exception)));

        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterRunningAndExposeExceptionWhenFailingToClose() throws Exception {
        doThrow(ioException).when(closable).close();

        final IOException e = expectThrows(IOException.class, this::runConsumer);

        assertThat(e, is(sameInstance(ioException)));
    }

    @Test
    public void shouldCloseAfterRunningAndSupressExceptionWhenFailingToClose() throws Exception {
        doThrow(exception).when(consumer).tryAccept(closable);
        doThrow(ioException).when(closable).close();

        final Exception e = expectThrows(Exception.class, this::runConsumer);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    public void runConsumer() throws Exception {
        tryWith(closable, consumer);
    }

    @Test
    public void shouldCloseAfterProvidingWithoutException() throws Exception {
        doReturn(value).when(function).tryApply(closable);

        final Object actual = runSupplier();
        assertThat(actual, is(sameInstance(value)));

        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterProvidingWithException() throws Exception {
        doThrow(exception).when(function).tryApply(closable);

        final Exception e = expectThrows(Exception.class, this::runSupplier);

        assertThat(e, is(sameInstance(exception)));

        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterProvidingAndExposeExceptionWhenFailingToClose() throws Exception {
        doReturn(value).when(function).tryApply(closable);
        doThrow(ioException).when(closable).close();

        final Exception e = expectThrows(Exception.class, this::runSupplier);

        assertThat(e, is(sameInstance(ioException)));

        verify(function).tryApply(closable);
        verify(closable).close();
    }

    @Test
    public void shouldCloseAfterProvidingAndSupressExceptionWhenFailingToClose() throws Exception {
        doThrow(exception).when(function).tryApply(closable);
        doThrow(ioException).when(closable).close();

        final Exception e = expectThrows(Exception.class, this::runSupplier);

        assertThat(e, is(sameInstance(exception)));
        assertThat(e.getSuppressed(), hasItemInArray(sameInstance(ioException)));
    }

    public Object runSupplier() throws Exception {
        return tryWith(closable, function);
    }

}
