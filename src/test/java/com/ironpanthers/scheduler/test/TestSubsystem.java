package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Command;
import com.ironpanthers.scheduler.command.Subsystem;

public class TestSubsystem extends Subsystem {

    public Command defaultCommand;

    public TestSubsystem() {
        this(null);
    }

    public TestSubsystem(Command defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    @Override
    public void initDefaultCommand() {
        if (defaultCommand != null) {
            scheduler.addCommand(defaultCommand);
        }
    }

}
