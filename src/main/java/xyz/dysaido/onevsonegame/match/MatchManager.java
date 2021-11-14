package xyz.dysaido.onevsonegame.match;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.ring.Ring;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatchManager {

    private final OneVSOneGame plugin;
    private Match match;
    private final Handler handler = new Handler();

    public MatchManager(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    public Match create(Ring ring) {
        if (isNull()) {
            this.match = new Match(plugin, ring);
            if (!handler.isAlive()) handler.start();
            return match;
        } else throw new RuntimeException("The match has already been created. Please, you have to destroy previous match that you wanna create a new match.");
    }

    public Optional<Match> getMatch() {
        return Optional.ofNullable(match);
    }

    public void destroy() {
        if (handler.isAlive()) handler.stop();
        match = null;
    }

    public Optional<Handler> getHandler() {
        return Optional.of(handler);
    }

    public boolean isNull() {
        return match == null;
    }

    public class Handler {

        private ScheduledThreadPoolExecutor executor;
        private ScheduledFuture<?> future;
        private boolean alive = false;

        public void start() {
            if (this.future != null) {
                future.cancel(false);
            }

            if (this.executor == null) {
                this.executor = new ScheduledThreadPoolExecutor(10);
                this.executor.setRemoveOnCancelPolicy(true);

            }
            this.future = this.executor.scheduleAtFixedRate(match, 0L, 100L, TimeUnit.MILLISECONDS);
            this.alive = true;
        }

        public boolean isAlive() {
            return alive;
        }

        public void stop() {
            if (this.future != null) {
                future.cancel(true);
            }
            if (this.executor != null) {
                this.executor.shutdownNow();
            }
            this.alive = false;
        }

    }

}
