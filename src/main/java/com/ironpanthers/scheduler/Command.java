package com.ironpanthers.scheduler;

public abstract class Command {

    abstract void initialize();
    abstract void loop();
    abstract boolean shouldTerminate();
    abstract void terminate();

}
