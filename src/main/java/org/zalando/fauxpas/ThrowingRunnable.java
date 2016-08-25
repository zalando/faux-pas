package org.zalando.fauxpas;

@FunctionalInterface
public interface ThrowingRunnable<X extends Throwable> {

    void tryRun() throws X;

    default Runnable with(final Strategy strategy) {
        return strategy.adapt(this);
    }

}
