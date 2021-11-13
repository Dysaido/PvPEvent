package xyz.dysaido.onevsonegame.match;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Match implements Runnable {

    private final Ring mRing;
    private final MatchQueue queue;
    private final OneVSOneGame plugin;
    private final Lock lock = new ReentrantLock();
    private MatchState mState = MatchState.STARTING;

    public Match(OneVSOneGame plugin, Ring ring) {
        this.plugin = plugin;
        this.mRing = ring;
        this.queue = new MatchQueue(this);
        init();
    }

    @Override
    public void run() {
        lock.lock();
        try {
        } finally {
            lock.unlock();
        }
    }

    private void init() {
        switch (mState) {
            case STARTING:
                starting();
                break;
            case START:
                start();
                break;
            case FIGHTING:
                fighting();
                break;
            case ENDING:
                ending();
                break;
            case END:
                end();
                break;
            default:
                plugin.getMatchManager().destroy();
        }
    }

    public void join(Player player) {
        Objects.requireNonNull(player);
        MatchPlayer matchPlayer = new MatchPlayer(this, player);
        queue.addQueue(matchPlayer);
        Format.broadcast(ChatColor.AQUA + player.getName() + " joined the event!");
    }

    public void leave(Player player) {
        Objects.requireNonNull(player);
        MatchPlayer matchPlayer = queue.findMatchPlayerByPlayer(player);
        if (matchPlayer != null) {
            queue.fullRemove(matchPlayer);
            Format.broadcast(ChatColor.AQUA + player.getName() + " left the event!");
        }
    }

    public void starting() {

    }

    public void start() {

    }

    public void fighting() {
        Pair<MatchPlayer, MatchPlayer> opponents = queue.getRandomizedOpponents();
        MatchPlayer damager = opponents.getKey();
        MatchPlayer victim = opponents.getValue();
    }

    public void ending() {

    }

    public void end() {

    }

    public MatchQueue getQueue() {
        return queue;
    }

    public Ring getRing() {
        return mRing;
    }

    public MatchState getState() {
        return mState;
    }

    public void setState(MatchState state) {
        this.mState = state;
    }
}
