package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.async.AsyncEventLoop;
import com.ironpanthers.scheduler.async.CallbackNotifyTask;

public class EventLoopTest {

    public static void main(String[] args) {
        AsyncEventLoop eventLoop = new AsyncEventLoop();

        eventLoop.sequential(new CallbackNotifyTask[] {
                (cb, l) -> {
                    System.out.println("first");
                    l.setTimeout(1000, cb);
                },
                (cb, l) -> {
                    System.out.println("another first");
                    l.setTimeout(1000, cb);
                },
                (cb, l) -> {
                    System.out.println("not first");
                    l.setTimeout(1000, cb);
                }
        });

        eventLoop.run();
    }

}
