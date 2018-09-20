package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.async.AsyncEventLoop;

public class TestSequentialAsync {

    public static void main(String[] args) throws InterruptedException {
        AsyncEventLoop loop = new AsyncEventLoop();

        loop.setInterval(1000, 1000, l -> {
            System.out.println("memes my dude");
        });
        loop.run();
    }

}
