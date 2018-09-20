package com.ironpanthers.scheduler.async;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Does cooperative multitasking to run lots of things at once in a single thread. Heavily inspired by JavaScript.
 *
 */
public class AsyncEventLoop {

    public final ExecutorService executor = Executors.newCachedThreadPool();
    private BlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();
    private Thread runningThread;

    private HashMap<Long, Future<Void>> intervals = new HashMap<>();
    private long nextIntervalId = 0;

    public void scheduleCommand(AsyncTask cmd) {
        queue.add(cmd);
    }

    /**
     * Runs all commands submitted to this event loop. Blocks until shut down.
     */
    public synchronized void run() {
        if (runningThread != null) {
            throw new InvalidStateException("This event loop is already running!");
        }
        try {
            runningThread = Thread.currentThread();
            while (true) {
                AsyncTask cmd = queue.take();
                cmd.execute(this);
            }
        } catch (InterruptedException ignored) {

        }
        queue.clear();
    }

    /**
     * Stop running the dude
     */
    public void shutdown() {
        runningThread.interrupt();
    }

    /**
     * Run a callback function some time in the future.
     * @param time It will be run at least this many milliseconds in the future.
     * @param run What to run.
     */
    public void setTimeout(long time, AsyncTask run) {
        executor.submit(() -> {
            try {
                Thread.sleep(time);
                scheduleCommand(run);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Run a function periodically.
     * @param delay Initial delay before first call.
     * @param period Delay between calls after first call.
     * @param run What to run.
     * @return An ID to cancel this, if you wish to do so
     */
    public long setInterval(long delay, long period, AsyncTask run) {
        Future<Void> future = executor.submit(() -> {
            Thread.sleep(delay);
            while (true) {
                scheduleCommand(run);
                Thread.sleep(period);
            }
        });
        long id = nextIntervalId++;
        intervals.put(id, future);
        return id;
    }

    /**
     * Cancel an interval that was running.
     * @param intervalId the interval to cancel
     */
    public void clearInterval(long intervalId) {
        intervals.get(intervalId).cancel(true);
    }

    public void parallelAny(CallbackNotifyTask[] tasks, AsyncTask onComplete) {
        TaskCompletedCallback cb = new TaskCompletedCallback() {
            private boolean called = false;

            @Override
            public void finish() {
                if (!called) {
                    scheduleCommand(onComplete);
                    called = true;
                }
            }
        };
        for (CallbackNotifyTask t: tasks) {
            scheduleCommand(l -> t.execute(cb, l));
        }
    }

}
