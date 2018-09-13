package com.ironpanthers.scheduler.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Globals {

    public static final ExecutorService executor = Executors.newCachedThreadPool();

}
