package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubsystemDefaultTest {

    Scheduler scheduler = new Scheduler();
    TestSubsystem sa = new TestSubsystem();
    TestCommand ca = new TestCommand(sa);
    TestCommand cb = new TestCommand(sa);

    @Before
    public void setUp() {
        sa.setDefaultCommand(ca);
        scheduler.registerSubsystem(sa);
    }

    @Test
    public void testInitCommand() {
        scheduler.run();
        Assert.assertEquals(ca, sa.getCurrentCommand());
        Assert.assertEquals(0, ca.timesTerminated);

        ca.shouldRunNextLoop = false;
        scheduler.run();
        Assert.assertEquals(ca, sa.getCurrentCommand());
        Assert.assertEquals(1, ca.timesTerminated);
        Assert.assertTrue(ca.wasInterrupted);

        scheduler.addCommand(cb);
        scheduler.run();
        Assert.assertEquals(cb, sa.getCurrentCommand());
        Assert.assertEquals(1, cb.timesLooped);
        Assert.assertEquals(2, ca.timesTerminated);

        ca.reset();
        cb.shouldRunNextLoop = false;
        scheduler.run();
        Assert.assertEquals(ca, sa.getCurrentCommand());
        Assert.assertEquals(1, cb.timesTerminated);
    }

}
