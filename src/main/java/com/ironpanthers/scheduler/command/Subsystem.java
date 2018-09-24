package com.ironpanthers.scheduler.command;

import java.util.Set;
import java.util.function.Supplier;

public abstract class Subsystem {

    Command currentCommand;
    protected Scheduler scheduler;
    boolean hasNewCommand = false;

    final void setCurrentCommand(Command currentCommand) {
        this.currentCommand = currentCommand;
    }

    public final Command getCurrentCommand() {
        return currentCommand;
    }

    /**
     * Create the default command.
     * @return Executed every time a default command is needed.
     */
    protected abstract Command createDefaultCommand();

    final Command getDefaultCommand() {
        Command command = createDefaultCommand();
        Set<Subsystem> required = command.getRequiredSubsystems();
        if (!required.contains(this)) {
            throw new IllegalArgumentException("A subsystem's default command must require the subsystem!");
        }
        if (required.size() > 1) {
            throw new IllegalArgumentException("A default command must require only 1 subsystem!");
        }
        return command;
    }

}
