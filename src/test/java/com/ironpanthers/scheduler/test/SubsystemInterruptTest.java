package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import junit.framework.TestCase;
import org.junit.Test;

public class SubsystemInterruptTest extends TestCase {

    Scheduler scheduler = new Scheduler();
    TestSubsystem sa = new TestSubsystem();

    @Override
    protected void setUp() throws Exception {
        scheduler.registerSubsystem(sa);
    }

    @Test
    public void testSubsystemInterrupt() {
        TestCommand ca = new TestCommand(sa);
        TestCommand cb = new TestCommand(sa);

        scheduler.addCommand(ca);
        scheduler.run();
        assertEquals(1, ca.timesLooped);
        assertEquals(0, cb.timesLooped);
        assertEquals(ca, sa.getCurrentCommand());

        scheduler.addCommand(cb);
        scheduler.run();
        assertEquals(cb, sa.getCurrentCommand());
        assertEquals(1, ca.timesTerminated);
        assertTrue(ca.wasInterrupted);
    }

}
