package com.ironpanthers.scheduler.async;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * <p>Uses cooperative multitasking to run lots of things at once in a single thread. Heavily inspired by JavaScript and
 * the async.js library.</p>
 * <p>Since it is inspired by JavaScript, it also suffers from the same drawbacks. You should not use
 * the event loop for frequent, heavy computation. It is better at doing many small things (like waiting for x ms or for a
 * command to finish) than it is at doing a few big things (like PID, blocking operations).</p>
 * <p>To do a blocking operation, you should take a thread from the included executor, run the blocking operation inside
 * that thread, and schedule a task on this event loop after the operation finishes.</p>
 */
public class AsyncEventLoop {

    public final ExecutorService executor = Executors.newCachedThreadPool();
    private BlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();
    private Thread runningThread;

    private HashMap<Long, Future<Void>> intervals = new HashMap<>();
    private long nextIntervalId = 0;

    public void scheduleTask(AsyncTask cmd) {
        queue.add(cmd);
    }

    /**
     * Runs all commands submitted to this event loop. Blocks until shut down.
     */
    public synchronized void run() {
        if (runningThread != null) {
            throw new IllegalStateException("This event loop is already running!");
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
                scheduleTask(run);
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
                scheduleTask(run);
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

    /**
     * Run a set of tasks all at once.
     * @param tasks the tasks to run
     * @param onComplete what is called when any one of the tasks finishes. It is called once and only once.
     */
    public void parallelAny(CallbackNotifyTask[] tasks, AsyncTask onComplete) {
        TaskCompletedCallback cb = new ParallelAnyCallback(onComplete);
        for (CallbackNotifyTask t: tasks) {
            scheduleTask(l -> t.execute(cb, l));
        }
    }

    /**
     * Run a set of tasks all at once.
     * @param tasks the tasks to run
     * @param onComplete what is called when all of the tasks finishes.
     */
    public void parallelAll(CallbackNotifyTask[] tasks, AsyncTask onComplete) {
        TaskCompletedCallback cb = new ParallelAllCallback(tasks, onComplete);
        for (CallbackNotifyTask t: tasks) {
            scheduleTask(l -> t.execute(cb, l));
        }
    }

    /**
     * Run a set of tasks, one after another.
     * @param tasks
     */
    public void sequential(CallbackNotifyTask[] tasks) {
        TaskCompletedCallback cb = new SequentialCallback(tasks);
        scheduleTask(l -> tasks[0].execute(cb, l));
    }

    private class SequentialCallback implements TaskCompletedCallback {
        private final CallbackNotifyTask[] tasks;
        private int index;

        SequentialCallback(CallbackNotifyTask[] tasks) {
            this.tasks = tasks;
            index = 0;
        }

        @Override
        public void finish() {
            index++;
            if (index < tasks.length) {
                scheduleTask(l -> tasks[index].execute(this, l));
            } else {
                throw new IllegalStateException("This callback was called multiple times in one CallbackNotifyTask!");
            }
        }
    }

    private class ParallelAllCallback implements TaskCompletedCallback {
        private final CallbackNotifyTask[] tasks;
        private final AsyncTask onComplete;
        private int left;

        ParallelAllCallback(CallbackNotifyTask[] tasks, AsyncTask onComplete) {
            this.tasks = tasks;
            this.onComplete = onComplete;
            left = tasks.length;
        }

        @Override
        public void finish() {
            left--;
            if (left == 0) {
                scheduleTask(onComplete);
            } else if (left < 0) {
                throw new IllegalStateException("This callback was called multiple times in one CallbackNotifyTask!");
            }
        }
    }

    private class ParallelAnyCallback implements TaskCompletedCallback {
        private final AsyncTask onComplete;
        private boolean called;

        ParallelAnyCallback(AsyncTask onComplete) {
            this.onComplete = onComplete;
            called = false;
        }

        @Override
        public void finish() {
            if (!called) {
                scheduleTask(onComplete);
                called = true;
            }
        }
    }
}
