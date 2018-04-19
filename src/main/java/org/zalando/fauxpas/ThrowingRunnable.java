package org.zalando.fauxpas;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@FunctionalInterface
public interface ThrowingRunnable<X extends Throwable> extends Runnable {

    void tryRun() throws X;

    @Override
    @SneakyThrows
    default void run() {
        tryRun();
    }

}
