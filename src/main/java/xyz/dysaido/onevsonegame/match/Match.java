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
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Match extends MatchTask {

    private final Ring ring;
    private final MatchQueue queue;
    private final OneVSOneGame plugin;
    private final Lock lock = new ReentrantLock();
    private MatchState state = MatchState.WAITING;
    private int round = 0;

    private long lastTransaction = 0;

    private int waiting = 30;
    private int starting = 5;
    private int ending = 5;

    public Match(OneVSOneGame plugin, Ring ring) {
        super("Match");
        this.plugin = plugin;
        this.ring = ring;
        this.queue = new MatchQueue(this);
    }

    @Override
    public void run() {
        /*this.lastTick = this.tick;
        this.tick = System.currentTimeMillis();
        if (this.tick - this.lastTransaction >= 250L) {
            this.lastTransaction = this.tick;
            // Here task
        }*/
        lock.lock();
        try {
            long tick = System.currentTimeMillis();
            if (tick - this.lastTransaction >= 1000) {
                this.lastTransaction = tick;
                System.out.println("Tasking...");
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
                        stop();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void join(Player player) {
        Objects.requireNonNull(player);
        if (waiting > 0) {
            if (!queue.contains(player)) {
                MatchPlayer matchPlayer = new MatchPlayer(this, player);
                queue.addMatchPlayer(matchPlayer);
                Format.broadcast(ChatColor.AQUA + player.getName() + " joined the event!");
            } else {
                player.sendMessage("You are already joined");
            }
        } else {
            player.sendMessage("Lose");
        }
    }

    public void leave(Player player) {
        Objects.requireNonNull(player);
        MatchPlayer matchPlayer = queue.findByPlayer(player);
        if (matchPlayer != null) {
            queue.removeMatchPlayer(matchPlayer);
            matchPlayer.reset(ring.getWorldSpawn());
            Format.broadcast(ChatColor.AQUA + player.getName() + " left the event!");
        }
    }

    public void waiting() {
        if (waiting % 5 == 0) {
            Format.broadcast("Join for event " + waiting + "sec");
            Format.broadcastClickable("&b[Click to join]");
        } else if (waiting < 5) {
            Format.broadcastClickable("&b[Click to join]");
        }
        waiting--;
        if (waiting <= 0) {
            if (queue.shouldDoEnd()) {
                setState(MatchState.ENDING);
            } else {
                Format.broadcast("Event join has been disabled");
                setState(MatchState.STARTING);
            }
        }
    }

    public void starting() {
        Format.broadcast("Event will be started " + starting + "sec");
        starting--;
        if (starting <= 0) {
            Format.broadcast("Event start");
            setState(MatchState.FIGHTING);
        }
    }

    public void fighting() {
        if (shouldNextRound()) {
            Optional<MatchPlayer> matchPlayer = queue.getPlayersByState(PlayerState.FIGHT).stream().findFirst();
            matchPlayer.ifPresent(internal -> internal.reset(ring.getLobby()));
            nextRound();
        } else if (queue.getPlayersByState(PlayerState.QUEUE).size() == 0 && !hasFighting()) {
            Optional<MatchPlayer> matchPlayer = queue.getPlayersByState(PlayerState.FIGHT).stream().findFirst();
            matchPlayer.ifPresent(internal -> {
                Format.broadcast("Event's winner " + internal.getPlayer().getName());
                internal.setState(PlayerState.WINNER);
                internal.reset(ring.getWorldSpawn());
            });
            if (queue.shouldDoEnd()) {
                queue.getMatchPlayers().stream().map(MatchPlayer::getPlayer).forEach(player -> player.teleport(ring.getWorldSpawn()));
                setState(MatchState.ENDING);
            }
        }
    }

    public void ending() {
        Format.broadcast("Event will be ended " + ending + "sec");
        ending--;
        if (ending <= 0) {
            Format.broadcast("Event is ended");
            stop();
            plugin.getMatchManager().destroy();
        }
    }

    private void nextRound() {
        this.round++;
        Pair<MatchPlayer, MatchPlayer> opponents = this.queue.randomizedOpponents();
        MatchPlayer damager = opponents.getKey();
        MatchPlayer victim = opponents.getValue();
        damager.setup(ring.getSpawn1());
        victim.setup(ring.getSpawn2());
    }

    private boolean hasFighting() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() == 2;
    }

    private boolean shouldNextRound() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() <= 1 && queue.getPlayersByState(PlayerState.QUEUE).size() > 0 && getState().equals(MatchState.FIGHTING) && !queue.shouldDoEnd();
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
