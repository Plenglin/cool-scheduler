package com.ironpanthers.scheduler.command;

import java.util.Set;
import java.util.function.Supplier;

public abstract class Subsystem {

    Command currentCommand;
    protected Scheduler scheduler;
    boolean hasNewCommand = false;

    private Supplier<Command> defaultCommandFactory = null;

    /**
     * Set the default command factory of this subsystem. Default commands must require this subsystem and only this subsystem.
     * @param factory the default command factory
     */
    protected void setDefaultCommandFactory(Supplier<Command> factory) {
        this.defaultCommandFactory = factory;
    }

    public Command createDefaultCommand() {
        if (defaultCommandFactory == null) return null;
        Command command = defaultCommandFactory.get();
        Set<Subsystem> required = command.getRequiredSubsystems();
        if (!required.contains(this)) {
            throw new IllegalArgumentException("A subsystem's default command must require the subsystem!");
        }
        if (required.size() > 1) {
            throw new IllegalArgumentException("A default command must require only 1 subsystem!");
        }
        return command;
    }

    public final Command getCurrentCommand() {
        return currentCommand;
    }

}
