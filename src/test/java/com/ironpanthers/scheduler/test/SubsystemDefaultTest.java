package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import junit.framework.TestCase;
import org.junit.Test;

public class SubsystemDefaultTest extends TestCase {

    Scheduler scheduler = new Scheduler();
    TestSubsystem sa = new TestSubsystem();
    TestCommand ca = new TestCommand(sa);

    @Override
    protected void setUp() throws Exception {
        sa.defaultCommand = ca;
        scheduler.registerSubsystem(sa);
    }

    @Test
    public void testInitCommand() {
        scheduler.run();
        assertEquals(ca, sa.getCurrentCommand());

        ca.shouldRunNextLoop = false;
        scheduler.run();
        assertEquals(ca, sa.getCurrentCommand());

        TestCommand cb = new TestCommand(sa);
        ca.reset();
        scheduler.addCommand(cb);
        scheduler.run();
        assertEquals(cb, sa.getCurrentCommand());
        assertEquals(1, cb.timesLooped);
    }

}
