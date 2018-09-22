package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class NoSubsystemTest {

    Scheduler scheduler = new Scheduler();

    @Test
    public void testLoop() {
        TestCommand ca = new TestCommand();
        TestCommand cb = new TestCommand();
        TestCommand cc = new TestCommand();

        scheduler.addCommand(ca);
        scheduler.run();
        Assert.assertEquals(1, ca.timesInitialized);
        Assert.assertEquals(1, ca.timesLooped);
        Assert.assertEquals(0, ca.timesTerminated);

        scheduler.run();
        Assert.assertEquals(1, ca.timesInitialized);
        Assert.assertEquals(2, ca.timesLooped);
        Assert.assertEquals(0, ca.timesTerminated);

        ca.shouldRunNextLoop = false;
        scheduler.run();
        Assert.assertEquals(1, ca.timesInitialized);
        Assert.assertEquals(3, ca.timesLooped);
        Assert.assertEquals(1, ca.timesTerminated);
        Assert.assertFalse(ca.wasInterrupted);

        scheduler.addCommand(cb);
        scheduler.addCommand(cc);
        scheduler.run();

        Assert.assertEquals(1, ca.timesInitialized);
        Assert.assertEquals(3, ca.timesLooped);
        Assert.assertEquals(1, ca.timesTerminated);

        Assert.assertEquals(1, cb.timesInitialized);
        Assert.assertEquals(1, cb.timesLooped);
        Assert.assertEquals(0, cb.timesTerminated);

        Assert.assertEquals(1, cc.timesInitialized);
        Assert.assertEquals(1, cc.timesLooped);
        Assert.assertEquals(0, cc.timesTerminated);

        cb.shouldRunNextLoop = false;
        scheduler.run();
        Assert.assertEquals(1, cb.timesInitialized);
        Assert.assertEquals(2, cb.timesLooped);
        Assert.assertEquals(1, cb.timesTerminated);
        Assert.assertFalse(cb.wasInterrupted);

        Assert.assertEquals(1, cc.timesInitialized);
        Assert.assertEquals(2, cc.timesLooped);
        Assert.assertEquals(0, cc.timesTerminated);
    }

}
