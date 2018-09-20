package com.ironpanthers.scheduler.async;

@FunctionalInterface
public interface AsyncTask {

    void execute(AsyncEventLoop loop);

}
