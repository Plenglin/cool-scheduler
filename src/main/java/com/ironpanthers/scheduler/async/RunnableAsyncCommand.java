package com.ironpanthers.scheduler.async;

public class RunnableAsyncCommand extends AsyncCommand {

    private Runnable run;

    public RunnableAsyncCommand(Runnable run) {
        this.run = run;
    }

    @Override
    public void executeCommand(AsyncEventLoop eventLoop) {
        run.run();
        finish();
    }
}
