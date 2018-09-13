package com.ironpanthers.scheduler.async;

/**
 * A command that is run asynchronously with a {@link AsyncEventLoop}
 *
 * @see AsyncEventLoop
 */
@FunctionalInterface
public interface AsyncCommand {
    /**
     * Called when this command is pulled from the event queue.
     */
    void executeCommand(AsyncEventLoop eventLoop);

}
