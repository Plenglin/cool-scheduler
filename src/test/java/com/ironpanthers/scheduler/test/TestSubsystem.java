package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Command;
import com.ironpanthers.scheduler.command.Subsystem;

public class TestSubsystem extends Subsystem {

    public Command defaultCommand;

    @Override
    protected Command createDefaultCommand() {
        return defaultCommand;
    }
}
