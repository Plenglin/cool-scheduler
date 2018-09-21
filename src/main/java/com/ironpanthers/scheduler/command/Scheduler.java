package com.ironpanthers.scheduler.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler implements Iterable<Command> {

    private LinkedBlockingQueue<Command> commandsToAdd = new LinkedBlockingQueue<>();
    private SentinelNode sentinel = new SentinelNode();
    private Set<Subsystem> subsystems = new HashSet<>();

    public void registerSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    private void addNewCommands() {
        Command command = sentinel.prev;
        while (!commandsToAdd.isEmpty()) {
            Command newCommand = commandsToAdd.poll();
            newCommand._initialize();
            command.append(newCommand);
            command = newCommand;
        }
    }

    private void executeCurrentCommands() {
        Command command = sentinel.next;
        while (command != sentinel) {
            if (!command.isInitialized()) {
                command._initialize();
            }
            if (!command._loop()) {
                command._terminate(false);
            }
        }
    }

    /**
     * Run 1 iteration of this scheduler.
     */
    public void run() {
        addNewCommands();
        executeCurrentCommands();
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
