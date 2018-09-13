package com.ironpanthers.scheduler.async;


import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

/**
 * Runs multiple {@link AsyncCommand}s at once.
 */
public class ParallelAsyncCommand extends AsyncCommand {

    public enum WaitMode {
        /**
         * Finishes the {@link ParallelAsyncCommand} after one of the sub-commands is finished.
         */
        ANY,
        /**
         * Finishes the {@link ParallelAsyncCommand} after every sub-command finishes.
         */
        ALL
    }

    private final AsyncCommand[] commands;
    private final CountDownLatch latch;

    public ParallelAsyncCommand(WaitMode mode, AsyncCommand... commands) {
        this.commands = commands;
        latch = new CountDownLatch(mode == WaitMode.ALL ? commands.length : 1);
    }

    @Override
    public void executeCommand(AsyncEventLoop eventLoop) {
        Stream<Runnable> s = Arrays.stream(commands).map(cmdObject -> () -> {
            eventLoop.scheduleCommand(cmdObject);
            try {
                cmdObject.waitUntilFinished();
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        s.forEach(Globals.executor::submit);
        Globals.executor.submit(() -> {
            try {
                latch.await();
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
