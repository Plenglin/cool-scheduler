package com.ironpanthers.scheduler.command;

import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler {

    private LinkedBlockingQueue<Command> commands = new LinkedBlockingQueue<>();


    public void registerSubsystem(Subsystem subsystem) {

    }

    /**
     * Run this scheduler for 1 iteration.
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {
        Command command = commands.take();
        if (command.call(this)) {
            commands.put(command);
        } else {
            //command.terminate();
        }
    }

}
