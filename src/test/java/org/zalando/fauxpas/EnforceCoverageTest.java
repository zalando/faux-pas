package org.zalando.fauxpas;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@Hack
@OhNoYouDidnt
@RunWith(JUnitPlatform.class)
public final class EnforceCoverageTest {

    @Test
    public void shouldUseFauxPasConstructor() {
        new FauxPas();
    }

    @Test
    public void shouldUseDefaultLoggingConstructor() {
        new FauxPas.DefaultLogging();
    }

    @Test
    public void shouldUseDefaultRethrowConstructor() {
        new FauxPas.DefaultRethrow();
    }

    @Test
    public void shouldUseSneakilyConstructor() {
        new FauxPas.Sneakily();
    }

    @Test
    public void shouldUseTryWithConstructor() {
        new TryWith();
    }

}
