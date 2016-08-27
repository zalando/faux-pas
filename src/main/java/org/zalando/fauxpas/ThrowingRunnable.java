package org.zalando.fauxpas;

import lombok.SneakyThrows;

@FunctionalInterface
public interface ThrowingRunnable<X extends Throwable> extends Runnable {

    void tryRun() throws X;

    @Override
    @SneakyThrows
    default void run() {
        tryRun();
    }

}
