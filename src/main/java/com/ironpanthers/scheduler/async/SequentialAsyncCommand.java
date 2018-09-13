package com.ironpanthers.scheduler.async;

import java.util.concurrent.ExecutorService;

/**
 * Runs several {@link AsyncCommand}s, one after another.
 */
public class SequentialAsyncCommand implements AsyncCommand {

    private AsyncCommand[] commands;
    private ExecutorService service;

    public SequentialAsyncCommand(ExecutorService service, AsyncCommand... commands) {
        this.service = service;
        this.commands = commands;
    }

    @Override
    public synchronized void executeCommand(AsyncEventLoop eventLoop) {
        service.submit(() -> {
            for (AsyncCommand cmd : commands) {
                try {
                    eventLoop.scheduleCommand(cmd);
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (cmd) {
                        cmd.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
