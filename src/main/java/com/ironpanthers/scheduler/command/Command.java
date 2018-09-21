package com.ironpanthers.scheduler.command;

public abstract class Command {

    private final Object lock = new Object();
    private double lastCalled = 0.0;
    private boolean initialized = false;
    protected Scheduler scheduler;

    /**
     * Blocks the current thread until this command is terminated.
     * @throws InterruptedException
     */
    public final void waitUntilFinished() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }

    /**
     * Called once and only once shortly after this command is added to Scheduler.
     */
    public abstract void onInitialize();

    /**
     * Called as fast as possible.
     * @param dt time since this was last called
     * @return should this command be called again?
     */
    public abstract boolean onLoop(double dt);

    /**
     * Called once and only once after onLoop returns false.
     * @param interrupted was this command interrupted by another command?
     */
    public abstract void onTerminate(boolean interrupted);

    void initialize(Scheduler scheduler) {
        lastCalled = System.currentTimeMillis();
        this.scheduler = scheduler;
    }

    boolean call(Scheduler scheduler) {
        if (!initialized) {
            initialize(scheduler);
            initialized = true;
        }
        boolean shouldCallAgain = onLoop(System.currentTimeMillis() - lastCalled);
        lastCalled = System.currentTimeMillis();
        return shouldCallAgain;
    }

    void terminateNormally() {
        onTerminate(false);
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
