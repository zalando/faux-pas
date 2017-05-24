package org.zalando.fauxpas;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public final class EnforceCoverageTest {

    @Test
    public void shouldUseFauxPasConstructor() {
        new FauxPas();
    }

    @Test
    public void shouldUseTryWithConstructor() {
        new TryWith();
    }

}
