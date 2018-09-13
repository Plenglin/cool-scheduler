package com.ironpanthers.scheduler.async;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Does cooperative multitasking to run lots of {@link AsyncCommand}s in a single thread.
 *
 * @see AsyncCommand
 */
public class AsyncEventLoop {

    private BlockingQueue<AsyncCommand> queue = new LinkedBlockingQueue<>();
    private boolean isRunning = true;

    public void scheduleCommand(AsyncCommand cmd) {
        queue.add(cmd);
    }

    public void run() throws InterruptedException {
        while (isRunning) {
            AsyncCommand cmd = queue.take();
            synchronized (cmd) {
                cmd.executeCommand(this);
                cmd.notifyAll();
            }
        }
        queue.clear();
    }

    public void doShutdown() {
        isRunning = false;
    }

}
