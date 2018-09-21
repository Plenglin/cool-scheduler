package com.ironpanthers.scheduler.command;

import java.util.HashSet;
import java.util.Set;

public abstract class Command {

    private final Object lock = new Object();
    private long lastCalled = 0L;
    private Set<Subsystem> requiredSubsystems = new HashSet<Subsystem>();
    private boolean isInitialized = false;
    private boolean isTerminated = false;
    Command next = null;
    Command prev = null;

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public Set<Subsystem> getRequiredSubsystems() {
        return requiredSubsystems;
    }

    private void remove() {
        prev.next = next;
        next.prev = prev;
    }

    void append(Command command) {
        command.prev = this;
        next = command;
    }

    protected void require(Subsystem subsystem) {
        requiredSubsystems.add(subsystem);
    }

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
    public void onInitialize() {}

    /**
     * Called as fast as possible.
     * @param dt time since this was last called
     * @return should this command be called again?
     */
    public boolean onLoop(double dt) { return false; }

    /**
     * Called once and only once after onLoop returns false.
     * @param interrupted was this command interrupted by another command?
     */
    public void onTerminate(boolean interrupted) {}

    final void _initialize() {
        onInitialize();
        isInitialized = true;
        for (Subsystem subsystem: requiredSubsystems) {
            subsystem.getCurrentCommand()._terminate(true);
        }
    }

    final boolean _loop() {
        long currentTime = System.currentTimeMillis();
        boolean result = onLoop((currentTime - lastCalled) / 1000.0);
        lastCalled = currentTime;
        return result;
    }

    final void _terminate(boolean interrupted) {
        onTerminate(interrupted);
        isTerminated = true;
        remove();
    }
}
