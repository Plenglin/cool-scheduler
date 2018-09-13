package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.async.AsyncEventLoop;
import com.ironpanthers.scheduler.async.SequentialAsyncCommand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSequentialAsync {

    public static void main(String[] args) throws InterruptedException {
        AsyncEventLoop loop = new AsyncEventLoop();
        ExecutorService exec = Executors.newCachedThreadPool();
        SequentialAsyncCommand seq = new SequentialAsyncCommand(exec,
                l -> System.out.println("1"),
                l -> System.out.println("2"),
                l -> System.out.println("3")
                );
        loop.scheduleCommand(seq);
        loop.run();
    }

}
