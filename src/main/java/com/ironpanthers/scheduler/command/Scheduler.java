package com.ironpanthers.scheduler.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Scheduler implements Iterable<Command> {

    private int currentIteration = 0;
    private ConcurrentLinkedQueue<Command> commandsToAdd = new ConcurrentLinkedQueue<>();
    private SentinelNode sentinel = new SentinelNode();
    private Set<Subsystem> subsystems = new HashSet<>();

    public void reset() {
        commandsToAdd.clear();
        subsystems.clear();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    public void addCommand(Command command) {
        if (command != null) {
            commandsToAdd.add(command);
        }
    }

    public void registerSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
        subsystem.scheduler = this;
        addCommand(subsystem.getDefaultCommand());
    }

    private void addCommandsFromQueue(Command command) {
        while (!commandsToAdd.isEmpty()) {
            Command newCommand = commandsToAdd.remove();
            command.next = newCommand;
            newCommand.prev = command;
            command = newCommand;
        }
        command.next = sentinel;
        sentinel.prev = command;
    }

    private void removeLockingCommandsAfter(Command command) {
        // Remove commands that lock the subsystems.
        command = command.next;
        while (command != sentinel) {
            for (Subsystem subsystem: command.getRequiredSubsystems()) {
                Command currentCommand = subsystem.getCurrentCommand();
                // In the event that 2 commands are submitted requesting the same subsystem, the one submitted later
                // will take precedence. We will not terminate commands that have not yet been initialized.
                if (!subsystem.hasNewCommand && currentCommand != null) {
                    currentCommand._terminateInterrupted();
                }
                subsystem.hasNewCommand = true;
                subsystem.currentCommand = command;
            }
            command = command.next;
        }
    }

    private void initializeCommandsAfter(Command command) {
        // Initialize the remaining commands.
        command = command.next;
        while (command != sentinel) {
            command._initialize(this);
            command = command.next;
        }
    }

    private void addNewCommands() {
        if (commandsToAdd.isEmpty()) return;

        // Reset subsystems
        for (Subsystem subsystem: subsystems) {
            subsystem.hasNewCommand = false;
        }

        // Drain the elements of the queue to the scheduler.
        Command iterStart = sentinel.prev;

        addCommandsFromQueue(iterStart);
        removeLockingCommandsAfter(iterStart);
        initializeCommandsAfter(iterStart);
    }

    /**
     * Run 1 iteration of this scheduler.
     */
    public void run() {
        addNewCommands();

        Command command = sentinel.next;
        while (command != sentinel) {
            if (!command._loop()) {
                command._terminateNormally();
            }
            command = command.next;
        }

        for (Subsystem subsystem: subsystems) {
            if (!subsystem.hasNewCommand) {
                addCommand(subsystem.getDefaultCommand());
            }
            subsystem.hasNewCommand = false;
        }

        currentIteration++;
    }

    @Override
    public Iterator<Command> iterator() {
        return new Iterator<Command>() {
            Command currentCommand = sentinel.next;
            @Override
            public boolean hasNext() {
                return currentCommand == sentinel;
            }

            @Override
            public Command next() {
                Command out = currentCommand;
                currentCommand = currentCommand.next;
                return out;
            }
        };
    }

    private class SentinelNode extends Command {
        SentinelNode() {
            super();
            next = this;
            prev = this;
        }

        @Override
        public void onInitialize() {
            throw new IllegalStateException("SentinelNode should not be called!");
        }

        @Override
        public boolean onLoop(double dt) {
            throw new IllegalStateException("SentinelNode should not be called!");
        }

        @Override
        public void onTerminate(boolean interrupted) {
            throw new IllegalStateException("SentinelNode should not be called!");
        }
    }

}
