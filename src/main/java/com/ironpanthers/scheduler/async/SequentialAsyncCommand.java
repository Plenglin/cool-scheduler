package com.ironpanthers.scheduler.async;

import java.util.concurrent.ExecutorService;

/**
 * Runs several {@link AsyncCommand}s, one after another.
 */
public class SequentialAsyncCommand extends AsyncCommand {

    private AsyncCommand[] commands;

    public SequentialAsyncCommand(AsyncCommand... commands) {
        this.commands = commands;
    }

    @Override
    public synchronized void executeCommand(AsyncEventLoop eventLoop) {
        Globals.executor.submit(() -> {
            for (AsyncCommand cmd : commands) {
                try {
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (cmd) {
                        eventLoop.scheduleCommand(cmd);
                        cmd.waitUntilFinished();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
