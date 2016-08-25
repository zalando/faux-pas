package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;

@RunWith(JUnitPlatform.class)
public final class UncheckedTest {

    @Test
    public void shouldNotWrapError() {
        shouldNotWrap(new Error());
    }

    @Test
    public void shouldNotWrapRuntimeException() {
        shouldNotWrap(new RuntimeException());
    }

    public <X extends Throwable> void shouldNotWrap(final X expected) {
        final Function<Throwable, RuntimeException> unit = FauxPas.unchecked();

        final Throwable actual = expectThrows(Throwable.class, () -> {
            throw unit.apply(expected);
        });

        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    public void shouldWrapIOException() {
        shouldWrap(new IOException(), UncheckedIOException.class);
    }

    @Test
    public void shouldWrapThrowable() {
        shouldWrap(new Throwable(), RuntimeException.class);
    }

    public <X extends Throwable> void shouldWrap(final X expected, final Class<? extends Throwable> wrap) {
        final Function<Throwable, RuntimeException> unit = FauxPas.unchecked();

        final Throwable actual = expectThrows(Throwable.class, () -> {
            throw unit.apply(expected);
        });

        assertThat(actual, is(instanceOf(wrap)));
        assertThat(actual, hasFeature(Throwable::getCause, is(sameInstance(expected))));
    }

}
