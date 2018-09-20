package com.ironpanthers.scheduler.async;

/**
 * A task that notifies a callback when it finishes.
 */
@FunctionalInterface
public interface CallbackNotifyTask {

    /**
     * Execute this task. When the task is done, cb will be called.
     * @param cb the callback to be notified when this task finishes.
     * @param loop the event loop calling this task.
     */
    void execute(TaskCompletedCallback cb, AsyncEventLoop loop);

}
