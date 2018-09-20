package com.ironpanthers.scheduler.async;

@FunctionalInterface
public interface TaskCompletedCallback {

    void finish();

}
