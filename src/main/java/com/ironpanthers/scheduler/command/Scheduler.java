package com.ironpanthers.scheduler.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Scheduler implements Iterable<Command> {

    private ConcurrentLinkedQueue<Command> commandsToAdd = new ConcurrentLinkedQueue<>();
    private SentinelNode sentinel = new SentinelNode();
    private Set<Subsystem> subsystems = new HashSet<>();

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

    private void addNewCommands() {
        // Drain the elements of the queue to the scheduler.
        Command iterAdd = sentinel.prev;
        Command iterLock = sentinel.prev;  // reference for locking subsystems later
        Command iterInit = sentinel.prev;  // reference for initializing later
        while (!commandsToAdd.isEmpty()) {
            Command newCommand = commandsToAdd.remove();
            iterAdd.next = newCommand;
            newCommand.prev = iterAdd;
            iterAdd = newCommand;
        }
        iterAdd.next = sentinel;
        sentinel.prev = iterAdd;

        // Remove commands that lock the subsystems.
        iterLock = iterLock.next;
        while (iterLock != sentinel) {
            for (Subsystem subsystem: iterLock.getRequiredSubsystems()) {
                Command currentCommand = subsystem.getCurrentCommand();
                // In the event that 2 commands are submitted requesting the same subsystem, the one submitted later
                // will take precedence. We will not terminate commands that have not yet been initialized.
                if (!subsystem.hasNewCommand && currentCommand != null) {
                    currentCommand._terminateInterrupted();
                }
                subsystem.hasNewCommand = true;
                subsystem.currentCommand = iterLock;
            }
            iterLock = iterLock.next;
        }

        // Initialize the remaining commands.
        iterInit = iterInit.next;
        while (iterInit != sentinel) {
            iterInit._initialize(this);
            iterInit = iterInit.next;
        }
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
