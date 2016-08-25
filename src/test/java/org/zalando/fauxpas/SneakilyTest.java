package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

@RunWith(JUnitPlatform.class)
public final class SneakilyTest {

    @Test
    public void shouldSneakilyThrowError() {
        shouldSneakilyThrow(new Error());
    }

    @Test
    public void shouldSneakilyThrowRuntimeException() {
        shouldSneakilyThrow(new RuntimeException());
    }

    @Test
    public void shouldSneakilyThrowIOException() {
        shouldSneakilyThrow(new IOException());
    }

    @Test
    public void shouldSneakilyThrowThrowable() {
        shouldSneakilyThrow(new Throwable());
    }

    public <X extends Throwable> void shouldSneakilyThrow(final X expected) {
        final Function<Throwable, RuntimeException> unit = FauxPas.sneakily();

        final Throwable actual = expectThrows(Throwable.class, () -> {
            throw unit.apply(expected);
        });

        assertThat(actual, is(sameInstance(expected)));
    }

}
