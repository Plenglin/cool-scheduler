package com.ironpanthers.scheduler.async;


import java.util.concurrent.ExecutorService;

/**
 * Runs multiple
 */
public class ParallelAsyncCommand implements AsyncCommand {

    public enum WaitMode {
        ANY, ALL
    }

    private WaitMode mode;
    private ExecutorService service;
    private AsyncCommand[] commands;

    public ParallelAsyncCommand(WaitMode mode, ExecutorService service, AsyncCommand... commands) {
        this.mode = mode;
        this.service = service;
        this.commands = commands;
    }

    @Override
    public void executeCommand(AsyncEventLoop eventLoop) {

    }

}
