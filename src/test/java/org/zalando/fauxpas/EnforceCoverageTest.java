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
    public void shouldUseFauxPasConstructors() {
        new FauxPas();
    }

    @Test
    public void shouldUseDefaultLoggingConstructors() {
        new FauxPas.DefaultLogging();
    }

    @Test
    public void shouldUseDefaultRethrowConstructors() {
        new FauxPas.DefaultRethrow();
    }

    @Test
    public void shouldUseSneakilyConstructors() {
        new FauxPas.Sneakily();
    }

}
