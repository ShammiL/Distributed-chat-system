package org.example.services.coordination.schedulers;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ElectionScheduler implements IScheduler {

    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private long timeout = 10;

    @Override
    public void startWait() throws TimeoutException {
        isInterrupted.set(false);
        long startTime = System.currentTimeMillis();
        while (!isInterrupted.get()) {
            if (System.currentTimeMillis() - startTime > timeout) {
                throw new TimeoutException("election message wait timeout");
            }
        }
    }

    @Override
    public void interrupt() {
        isInterrupted.set(true);
    }
}
