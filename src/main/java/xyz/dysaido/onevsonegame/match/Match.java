package xyz.dysaido.onevsonegame.match;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Match implements Runnable {

    private final Ring ring;
    private final MatchQueue queue;
    private final OneVSOneGame plugin;
    private final Lock lock = new ReentrantLock();
    private MatchState state = MatchState.STARTING;
    private int round = 0;
    private Pair<MatchPlayer, MatchPlayer> opponents;
    public Match(OneVSOneGame plugin, Ring ring) {
        this.plugin = plugin;
        this.ring = ring;
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
        switch (state) {
            case WAITING:
                waiting();
                break;
            case STARTING:
                starting();
                break;
            case FIGHTING:
                fighting();
                break;
            case ENDING:
                ending();
                break;
            default:
                plugin.getMatchManager().destroy();
        }
    }

    public void join(Player player) {
        Objects.requireNonNull(player);
        MatchPlayer matchPlayer = new MatchPlayer(this, player);
        queue.addMatchPlayer(matchPlayer);
        Format.broadcast(ChatColor.AQUA + player.getName() + " joined the event!");
    }

    public void leave(Player player) {
        Objects.requireNonNull(player);
        MatchPlayer matchPlayer = queue.findByPlayer(player);
        if (matchPlayer != null) {
            queue.removeMatchPlayer(matchPlayer);
            matchPlayer.reset();
            Format.broadcast(ChatColor.AQUA + player.getName() + " left the event!");
        }
    }

    public void waiting() {

    }

    public void starting() {

    }

    public void fighting() {
        Pair<MatchPlayer, MatchPlayer> opponents = this.opponents;
        MatchPlayer damager = opponents.getKey();
        MatchPlayer victim = opponents.getValue();
        damager.setup();
        victim.setup();
    }

    public void ending() {

    }

    public void end() {

    }

    private void nextRound() {
        this.round++;
        if (queue.getPlayersByState(PlayerState.FIGHT).size() <= 0) {
            this.queue.randomizedOpponents();
            this.opponents = this.queue.getOpponent();
        }
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
