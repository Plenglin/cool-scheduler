package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubsystemDefaultTest {

    Scheduler scheduler = new Scheduler();
    TestSubsystem sa = new TestSubsystem();
    TestCommand ca = new TestCommand(sa);

    @Before
    public void setUp() {
        sa.setDefaultCommand(ca);
        scheduler.registerSubsystem(sa);
    }

    @Test
    public void testInitCommand() {
        scheduler.run();
        Assert.assertEquals(ca, sa.getCurrentCommand());

        ca.shouldRunNextLoop = false;
        scheduler.run();
        Assert.assertEquals(ca, sa.getCurrentCommand());

        TestCommand cb = new TestCommand(sa);
        ca.reset();
        scheduler.addCommand(cb);
        scheduler.run();
        Assert.assertEquals(cb, sa.getCurrentCommand());
        Assert.assertEquals(1, cb.timesLooped);
    }

}
