package org.zalando.fauxpas;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;

@Hack
@OhNoYouDidnt
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
