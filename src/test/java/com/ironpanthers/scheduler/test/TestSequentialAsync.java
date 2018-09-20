package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.async.AsyncEventLoop;
import com.ironpanthers.scheduler.async.CallbackNotifyTask;

public class TestSequentialAsync {

    public static void main(String[] args) {
        AsyncEventLoop loop = new AsyncEventLoop();

        loop.sequential(new CallbackNotifyTask[] {
                (cb, l) -> {
                    System.out.println("1");
                    loop.setTimeout(1000, cb);
                },
                (cb, l) -> {
                    System.out.println("2");
                    loop.setTimeout(1000, cb);
                },
                (cb, l) -> {
                    System.out.println("3");
                    loop.setTimeout(1000, cb);
                },
        });
        loop.run();
    }

}
