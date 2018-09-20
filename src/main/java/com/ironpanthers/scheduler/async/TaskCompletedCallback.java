package com.ironpanthers.scheduler.async;

@FunctionalInterface
public interface TaskCompletedCallback extends AsyncTask {

    void finish();

    @Override
    default void execute(AsyncEventLoop loop) {
        finish();
    }
}
