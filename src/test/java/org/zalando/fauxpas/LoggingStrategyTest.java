package org.zalando.fauxpas;

import org.junit.jupiter.api.Executable;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.withSettings;
import static org.zalando.fauxpas.FauxPas.loggingAnd;
import static org.zalando.fauxpas.FauxPas.rethrow;
import static org.zalando.fauxpas.FauxPas.sneakily;
import static org.zalando.fauxpas.FauxPas.throwingRunnable;

@RunWith(JUnitPlatform.class)
public final class LoggingStrategyTest extends StrategyTest {

    private final Logger logger = mock(Logger.class);
    private final Strategy strategy = mock(Strategy.class, withSettings().defaultAnswer(invocation ->
            invocation.getMethod().invoke(rethrow(sneakily()), invocation.getArguments())));

    @Test
    public void shouldProvideLoggingStrategy() {
        final Strategy unit = FauxPas.loggingAnd(strategy);
        final Exception expected = new Exception();
        final ThrowingRunnable<Exception> runnable = () -> {throw expected;};
        final Exception actual = expectThrows(Exception.class, throwingRunnable(runnable, unit)::run);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Override
    protected Strategy unit() {
        return loggingAnd(logger, strategy);
    }

    @Override
    protected void testOriginalWithException(final Throwable expected, final Executable executable) throws Throwable {
        final Exception actual = expectThrows(Exception.class, executable);
        assertThat(actual, is(sameInstance(expected)));
        verifyZeroInteractions(logger);
        verifyZeroInteractions(strategy);
    }

    @Override
    protected void testAdaptedWithException(final Throwable expected, final Executable executable) throws Throwable {
        final Exception actual = expectThrows(Exception.class, executable);
        assertThat(actual, is(sameInstance(expected)));
        verify(logger).error(anyString(), any(), same(expected));
    }

    @Override
    protected void testOriginalWithoutException(final Executable executable) throws Throwable {
        executable.execute();
        verifyZeroInteractions(logger);
        verifyZeroInteractions(strategy);
    }

    @Override
    protected void testAdaptedWithoutException(final Executable executable) throws Throwable {
        executable.execute();
        verifyZeroInteractions(logger);
    }

}