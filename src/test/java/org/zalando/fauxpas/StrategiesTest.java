package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

@RunWith(JUnitPlatform.class)
public final class StrategiesTest {

    @Test
    public void shouldProvideDefaultLoggingStrategy() {
        final Strategy unit = FauxPas.logging();
        assertThat(unit, is(notNullValue()));
    }

    @Test
    public void shouldProvideDefaultRethrowStrategy() {
        final Strategy unit = FauxPas.rethrow();
        final ThrowingRunnable<Exception> runnable = () -> {throw new Exception();};

        expectThrows(RuntimeException.class, runnable.with(unit)::run);
    }

}