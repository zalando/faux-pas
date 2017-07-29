package org.zalando.fauxpas;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@Hack
@OhNoYouDidnt
@RunWith(JUnitPlatform.class)
final class EnforceCoverageTest {

    @Test
    void shouldUseFauxPasConstructor() {
        new FauxPas();
    }

    @Test
    void shouldUseTryWithConstructor() {
        new TryWith();
    }

}
