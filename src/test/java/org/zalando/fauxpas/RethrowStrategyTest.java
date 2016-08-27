package org.zalando.fauxpas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Executable;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.fauxpas.FauxPas.rethrow;
import static org.zalando.fauxpas.FauxPas.throwingRunnable;

@RunWith(JUnitPlatform.class)
public final class RethrowStrategyTest extends StrategyTest {

    @SuppressWarnings("unchecked")
    private final Function<Throwable, RuntimeException> transformer = mock(Function.class);

    @BeforeEach
    public void defaultBehaviour() {
        when(transformer.apply(any())).thenAnswer(invocation -> {
            final Throwable argument = invocation.getArgument(0);
            return new IllegalStateException(argument);
        });
    }

    @Test
    public void shouldProvideDefaultRethrowStrategy() {
        final Strategy unit = FauxPas.rethrow();
        final Exception expected = new Exception();
        final ThrowingRunnable<Exception> runnable = () -> {throw expected;};
        final RuntimeException actual = expectThrows(RuntimeException.class, throwingRunnable(runnable, unit)::run);
        assertThat(actual.getCause(), is(sameInstance(expected)));
    }

    @Override
    protected Strategy unit() {
        return rethrow(transformer);
    }

    @Override
    protected void testOriginalWithException(final Throwable expected, final Executable executable) throws Throwable {
        final Exception actual = expectThrows(Exception.class, executable);
        assertThat(actual, is(sameInstance(expected)));
        verifyZeroInteractions(transformer);
    }

    @Override
    protected void testAdaptedWithException(final Throwable expected, final Executable executable) throws Throwable {
        expectThrows(IllegalStateException.class, executable);
        verify(transformer).apply(same(expected));
    }

    @Override
    protected void testOriginalWithoutException(final Executable executable) throws Throwable {
        executable.execute();
        verifyZeroInteractions(transformer);
    }

    @Override
    protected void testAdaptedWithoutException(final Executable executable) throws Throwable {
        executable.execute();
        verifyZeroInteractions(transformer);
    }

}