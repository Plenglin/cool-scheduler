package com.ironpanthers.scheduler.command;

public abstract class Subsystem {

    private Command currentCommand;
    protected Scheduler scheduler;

    public Command getCurrentCommand() {
        return currentCommand;
    }

    public void initDefaultCommand() {}

    public void setCurrentCommand(Command newCommand) {
        if (currentCommand != null) {
            currentCommand._terminate(true);
        }
        currentCommand = newCommand;
    }
}
