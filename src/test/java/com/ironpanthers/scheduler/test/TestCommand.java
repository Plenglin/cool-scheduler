package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Command;
import com.ironpanthers.scheduler.command.Subsystem;

public class TestCommand extends Command {

    private static int nextId = 0;

    public static void resetIdCounter() {
        nextId = 0;
    }

    public int timesInitialized = 0;
    public int timesLooped = 0;
    public int timesTerminated = 0;
    public boolean wasInterrupted = false;
    public boolean shouldRunNextLoop = true;
    public int id = nextId++;

    public TestCommand(Subsystem... requiredSubsystems) {
        for (Subsystem subsystem: requiredSubsystems) {
            require(subsystem);
        }
    }

    public void reset() {
        timesInitialized = 0;
        timesLooped = 0;
        timesTerminated = 0;
        wasInterrupted = false;
        shouldRunNextLoop = true;
    }

    @Override
    public void onInitialize() {
        timesInitialized++;
    }

    @Override
    public boolean onLoop(double dt) {
        timesLooped++;
        return shouldRunNextLoop;
    }

    @Override
    public void onTerminate(boolean interrupted) {
        wasInterrupted = interrupted;
        timesTerminated++;
    }

    @Override
    public String toString() {
        return "TestCommand(" + id + ")";
    }
}
