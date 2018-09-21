package com.ironpanthers.scheduler.command;

import com.sun.istack.internal.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler implements Iterable<Command> {

    private LinkedBlockingQueue<Command> commandsToAdd = new LinkedBlockingQueue<>();
    private SentinelNode sentinel = new SentinelNode();
    private Set<Subsystem> subsystems = new HashSet<>();

    public void addCommand(@NotNull Command command) {
        commandsToAdd.add(command);
    }

    public void registerSubsystem(@NotNull Subsystem subsystem) {
        subsystems.add(subsystem);
        subsystem.scheduler = this;
        subsystem.initDefaultCommand();
    }

    private void addNewCommands() {
        Command command = sentinel.prev;
        while (!commandsToAdd.isEmpty()) {
            Command newCommand = commandsToAdd.poll();
            newCommand._initialize();
            command.next = newCommand;
            newCommand.prev = command;
            for (Subsystem subsystem: newCommand.getRequiredSubsystems()) {
                subsystem.setCurrentCommand(newCommand);
            }
            command = newCommand;
        }
        command.next = sentinel;
        sentinel.prev = command;
    }

    /**
     * Run 1 iteration of this scheduler.
     */
    public void run() {
        addNewCommands();
        Command command = sentinel.next;
        while (command != sentinel) {
            if (!command._loop()) {
                command._terminate(false);
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
