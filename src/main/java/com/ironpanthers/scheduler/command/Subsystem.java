package com.ironpanthers.scheduler.command;

public abstract class Subsystem {

    private Command currentCommand;

    public Command getCurrentCommand() {
        return currentCommand;
    }

    abstract void initDefaultCommand();

}
