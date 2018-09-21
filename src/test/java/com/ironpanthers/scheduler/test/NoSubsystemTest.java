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
        assertEquals(ca.timesInitialized, 1);
        assertEquals(ca.timesLooped, 1);
        assertEquals(ca.timesTerminated, 0);

        scheduler.run();
        assertEquals(ca.timesInitialized, 1);
        assertEquals(ca.timesLooped, 2);
        assertEquals(ca.timesTerminated, 0);

        ca.shouldRunNextLoop = false;
        scheduler.run();
        assertEquals(ca.timesInitialized, 1);
        assertEquals(ca.timesLooped, 3);
        assertEquals(ca.timesTerminated, 1);
        assertFalse(ca.wasInterrupted);

        scheduler.addCommand(cb);
        scheduler.addCommand(cc);
        scheduler.run();

        assertEquals(ca.timesInitialized, 1);
        assertEquals(ca.timesLooped, 3);
        assertEquals(ca.timesTerminated, 1);

        assertEquals(cb.timesInitialized, 1);
        assertEquals(cb.timesLooped, 1);
        assertEquals(cb.timesTerminated, 0);

        assertEquals(cc.timesInitialized, 1);
        assertEquals(cc.timesLooped, 1);
        assertEquals(cc.timesTerminated, 0);

        cb.shouldRunNextLoop = false;
        scheduler.run();
        assertEquals(cb.timesInitialized, 1);
        assertEquals(cb.timesLooped, 2);
        assertEquals(cb.timesTerminated, 1);
        assertFalse(cb.wasInterrupted);

        assertEquals(cc.timesInitialized, 1);
        assertEquals(cc.timesLooped, 2);
        assertEquals(cc.timesTerminated, 0);
    }

}
