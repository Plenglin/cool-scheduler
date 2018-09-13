package com.ironpanthers.scheduler.async;

/**
 * A command that is run asynchronously with a {@link AsyncEventLoop}
 *
 * @see AsyncEventLoop
 */
public abstract class AsyncCommand {

    private final Object lock = new Object();
    private boolean running = true;

    /**
     * Blocks the current thread until this command is finished.
     *
     * @throws InterruptedException if it gets interrupted while blocking
     */
    public final void waitUntilFinished() throws InterruptedException {
        if (!running) {
            return;
        }
        synchronized (lock) {
            lock.wait();
        }
    }

    /**
     * Finish this command. Must be called when it's finished! If it isn't called when finished, you'll be in big danger!
     */
    protected final void finish() {
        synchronized (lock) {
            running = false;
            lock.notifyAll();
        }
    }

    /**
     * Called when this command is pulled from the event queue.
     */
    public abstract void executeCommand(AsyncEventLoop eventLoop);

}
