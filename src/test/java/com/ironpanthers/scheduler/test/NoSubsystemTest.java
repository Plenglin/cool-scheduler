package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.command.Scheduler;
import junit.framework.TestCase;
import org.junit.Test;


public class NoSubsystemTest extends TestCase {

    Scheduler scheduler = new Scheduler();

    @Test
    public void testLoop() {
        TestCommand ca = new TestCommand();
        TestCommand cb = new TestCommand();
        TestCommand cc = new TestCommand();

        scheduler.addCommand(ca);
        scheduler.run();
        assertEquals(1, ca.timesInitialized);
        assertEquals(1, ca.timesLooped);
        assertEquals(0, ca.timesTerminated);

        scheduler.run();
        assertEquals(1, ca.timesInitialized);
        assertEquals(2, ca.timesLooped);
        assertEquals(0, ca.timesTerminated);

        ca.shouldRunNextLoop = false;
        scheduler.run();
        assertEquals(1, ca.timesInitialized);
        assertEquals(3, ca.timesLooped);
        assertEquals(1, ca.timesTerminated);
        assertFalse(ca.wasInterrupted);

        scheduler.addCommand(cb);
        scheduler.addCommand(cc);
        scheduler.run();

        assertEquals(1, ca.timesInitialized);
        assertEquals(3, ca.timesLooped);
        assertEquals(1, ca.timesTerminated);

        assertEquals(1, cb.timesInitialized);
        assertEquals(1, cb.timesLooped);
        assertEquals(0, cb.timesTerminated);

        assertEquals(1, cc.timesInitialized);
        assertEquals(1, cc.timesLooped);
        assertEquals(0, cc.timesTerminated);

        cb.shouldRunNextLoop = false;
        scheduler.run();
        assertEquals(1, cb.timesInitialized);
        assertEquals(2, cb.timesLooped);
        assertEquals(1, cb.timesTerminated);
        assertFalse(cb.wasInterrupted);

        assertEquals(1, cc.timesInitialized);
        assertEquals(2, cc.timesLooped);
        assertEquals(0, cc.timesTerminated);
    }

}
