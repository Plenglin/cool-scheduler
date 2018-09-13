package com.ironpanthers.scheduler.test;

import com.ironpanthers.scheduler.async.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSequentialAsync {

    public static void main(String[] args) throws InterruptedException {
        AsyncEventLoop loop = new AsyncEventLoop();

        ParallelAsyncCommand par = new ParallelAsyncCommand(ParallelAsyncCommand.WaitMode.ANY,
                new RunnableAsyncCommand(() -> System.out.println("memes")),
                new SleepAsyncCommand(1000),
                new SleepAsyncCommand(100)
                );

        SequentialAsyncCommand seq = new SequentialAsyncCommand(
                new RunnableAsyncCommand(() -> System.out.println("begin")),
                par,
                new RunnableAsyncCommand(() -> System.out.println("end"))
        );

        loop.scheduleCommand(seq);
        loop.run();
    }

}
