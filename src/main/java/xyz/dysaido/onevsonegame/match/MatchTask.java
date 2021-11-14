package xyz.dysaido.onevsonegame.match;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class MatchTask {

    private final String name;
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture<?> future;
    public MatchTask(String name) {
        this.name = name;
    }

    public void start() {
        this.executor = new ScheduledThreadPoolExecutor(10);
        this.executor.setRemoveOnCancelPolicy(true);
        this.future = this.executor.scheduleAtFixedRate(this::run, 0L, 100L, TimeUnit.MILLISECONDS);

    }

    public void stop() {
        if (this.future != null) {
            future.cancel(true);
        }
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
    }

    public String getName() {
        return name;
    }

    public abstract void run();

}
