package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Command;
import com.ironpanthers.scheduler.command.Subsystem;

public class TestSubsystem extends Subsystem {

    public void setDefaultCommand(Command command) {
        setDefaultCommandFactory(() -> command);
    }

}
