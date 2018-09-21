package com.ironpanthers.scheduler.command;

public abstract class Subsystem {

    Command currentCommand;
    protected Scheduler scheduler;
    boolean hasNewCommand = false;

    private Command defaultCommand;

    protected void setDefaultCommand(Command command) {
        this.defaultCommand = command;
    }

    public Command getDefaultCommand() {
        return defaultCommand;
    }

    public final Command getCurrentCommand() {
        return currentCommand;
    }

}
