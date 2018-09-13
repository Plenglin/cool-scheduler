package com.ironpanthers.scheduler.async;


import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
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
    private final CyclicBarrier barrier;

    public ParallelAsyncCommand(WaitMode mode, AsyncCommand... commands) {
        this.commands = commands;
        latch = new CountDownLatch(mode == WaitMode.ALL ? commands.length : 1);
        barrier = new CyclicBarrier(commands.length + 1);
    }

    @Override
    public void executeCommand(AsyncEventLoop eventLoop) {
        Stream<Runnable> s = Arrays.stream(commands).map(cmdObject -> () -> {
            eventLoop.scheduleCommand(cmdObject);
            try {
                barrier.await();
                cmdObject.waitUntilFinished();
                latch.countDown();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });

        s.forEach(Globals.executor::submit);
        Globals.executor.submit(() -> {
            try {
                barrier.await();
                latch.await();
                finish();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
    }

}
