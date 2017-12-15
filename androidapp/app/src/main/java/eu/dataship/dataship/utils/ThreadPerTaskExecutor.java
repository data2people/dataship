package eu.dataship.dataship.utils;

import java.util.concurrent.Executor;

public class ThreadPerTaskExecutor implements Executor {
    public void execute( Runnable task ) {
        new Thread(task).start();
    }
}
