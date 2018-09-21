package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubsystemInterruptTest {

    Scheduler scheduler = new Scheduler();
    TestSubsystem sa = new TestSubsystem();

    @Before
    public void setUp() {
        scheduler.registerSubsystem(sa);
    }

    @Test
    public void testSubsystemInterrupt() {
        TestCommand ca = new TestCommand(sa);
        TestCommand cb = new TestCommand(sa);

        scheduler.addCommand(ca);
        scheduler.run();
        Assert.assertEquals(1, ca.timesLooped);
        Assert.assertEquals(0, cb.timesLooped);
        Assert.assertEquals(ca, sa.getCurrentCommand());

        scheduler.addCommand(cb);
        scheduler.run();
        Assert.assertEquals(cb, sa.getCurrentCommand());
        Assert.assertEquals(1, ca.timesTerminated);
        Assert.assertTrue(ca.wasInterrupted);
    }

}
