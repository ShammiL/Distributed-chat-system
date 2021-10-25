package org.example.services.coordination.schedulers;


import java.util.concurrent.TimeoutException;

public interface IScheduler {

    public void startWait() throws TimeoutException;
    public void interrupt();
}
