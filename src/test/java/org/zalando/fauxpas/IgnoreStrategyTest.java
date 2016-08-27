package org.zalando.fauxpas;

import org.junit.jupiter.api.Executable;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.expectThrows;

@RunWith(JUnitPlatform.class)
public class IgnoreStrategyTest extends StrategyTest {

    @Override
    protected Strategy unit() {
        return FauxPas.ignore();
    }

    @Override
    protected void testOriginalWithException(final Throwable expected, final Executable executable) throws Throwable {
        final Throwable actual = expectThrows(Throwable.class, executable);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Override
    protected void testAdaptedWithException(final Throwable expected, final Executable executable) throws Throwable {
        executable.execute();
    }

    @Override
    protected void testOriginalWithoutException(final Executable executable) throws Throwable {
        executable.execute();
    }

    @Override
    protected void testAdaptedWithoutException(final Executable executable) throws Throwable {
        executable.execute();
    }

}