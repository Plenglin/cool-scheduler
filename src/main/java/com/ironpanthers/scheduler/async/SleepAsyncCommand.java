package com.ironpanthers.scheduler.async;

public class SleepAsyncCommand implements AsyncCommand {

    private long time;

    public SleepAsyncCommand(long time) {
        this.time = time;
    }

    @Override
    public void executeCommand(AsyncEventLoop eventLoop) {

    }
}
