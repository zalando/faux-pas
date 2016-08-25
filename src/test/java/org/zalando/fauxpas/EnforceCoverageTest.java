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
    public void shouldUseStrategiesConstructors() {
        new Strategies();
    }

    @Test
    public void shouldUseStrategiesHolderConstructors() {
        new Strategies.Holder();
    }

}
