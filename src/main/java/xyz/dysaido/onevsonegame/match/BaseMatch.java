package xyz.dysaido.onevsonegame.match;

import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.setting.Settings;

public abstract class BaseMatch extends MatchTask {

    protected final Ring ring;
    protected final MatchQueue queue;
    protected volatile MatchState state = MatchState.WAITING;
    protected int round = 0;

    protected long lastTransaction = 0;

    protected int waiting = Settings.WAITING;
    protected int starting = Settings.STARTING;
    protected int ending = Settings.ENDING;

    public BaseMatch(String name, OneVSOneGame plugin, Ring ring) {
        super(name, plugin);
        this.ring = ring;
        this.queue = new MatchQueue(this);
    }

    @Override
    public void run() {
        long tick = System.currentTimeMillis();
        if (tick - this.lastTransaction >= 1000) {
            this.lastTransaction = tick;
            loop();
        }
    }

    public abstract void join(Player player);

    public abstract void leave(Player player);

    public abstract void loop();

    public abstract void nextRound();

    public abstract boolean hasFighting();

    public abstract boolean shouldEnd();

    public boolean shouldNextRound() {
        return queue.getPlayersByState(PlayerState.QUEUE).size() > 0 && getState().equals(MatchState.FIGHTING) && !shouldEnd();
    }

    public MatchQueue getQueue() {
        return queue;
    }

    public Ring getRing() {
        return ring;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }
}
