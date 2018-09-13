package com.ironpanthers.scheduler.async;

/**
 * Does nothing for a certain amount of time. Good for use in {@link SequentialAsyncCommand}.
 */
public class SleepAsyncCommand extends AsyncCommand {

    private final long time;

    public SleepAsyncCommand(long time) {
        this.time = time;
    }

    @Override
    public void executeCommand(AsyncEventLoop eventLoop) {
        Globals.executor.submit(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                finish();
            }
        });
    }
}
