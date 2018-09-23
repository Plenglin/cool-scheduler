package com.ironpanthers.scheduler.command;

import java.util.Set;

public abstract class Subsystem {

    Command currentCommand;
    protected Scheduler scheduler;
    boolean hasNewCommand = false;

    private Command defaultCommand;

    /**
     * Set the default command of this subsystem. Default commands must require this subsystem and only this subsystem.
     * @param command the default command
     */
    protected void setDefaultCommand(Command command) {
        Set<Subsystem> required = command.getRequiredSubsystems();
        if (!required.contains(this)) {
            throw new IllegalArgumentException("A subsystem's default command must require the subsystem!");
        }
        if (required.size() > 1) {
            throw new IllegalArgumentException("A default command must require only 1 subsystem!");
        }
        this.defaultCommand = command;
    }

    public Command getDefaultCommand() {
        return defaultCommand;
    }

    public final Command getCurrentCommand() {
        return currentCommand;
    }

}
