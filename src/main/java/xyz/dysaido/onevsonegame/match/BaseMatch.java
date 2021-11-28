package xyz.dysaido.onevsonegame.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.event.GamePlayerJoinEvent;
import xyz.dysaido.onevsonegame.event.GamePlayerLeaveEvent;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.Objects;

public abstract class BaseMatch extends MatchTask {

    protected final Ring ring;
    protected final MatchQueue queue;
    protected final MatchType type;
    protected volatile MatchState state = MatchState.WAITING;
    protected int round = 0;

    protected long lastTransaction = 0;

    protected int waiting = Settings.WAITING;
    protected int starting = Settings.STARTING;
    protected int ending = Settings.ENDING;

    public BaseMatch(String name, OneVSOneGame plugin, Ring ring) {
        super(name, plugin);
        this.ring = ring;
        this.type = ring.getMatchType();
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

    public void join(Player player) {
        Objects.requireNonNull(player);
        if (waiting > 0) {
            if (!queue.contains(player)) {
                MatchPlayer matchPlayer = new MatchPlayer(this, player);
                Bukkit.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent(this, matchPlayer));
                queue.addMatchPlayer(matchPlayer);
                Format.broadcast(Settings.JOIN_MESSAGE.replace("{player}", player.getName()));
            } else {
                player.sendMessage(Format.colored(Settings.ALREADY_JOINED_MESSAGE));
            }
        } else {
            player.sendMessage(Format.colored(Settings.EVENT_NOT_AVAILABLE_MESSAGE));
        }
    }

    public void leave(Player player) {
        Objects.requireNonNull(player);
        if (queue.contains(player)) {
            MatchPlayer matchPlayer = queue.findByPlayer(player);
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent(this, matchPlayer));
            matchPlayer.reset(ring.getWorldSpawn(), PlayerState.SPECTATOR);
            queue.removeMatchPlayer(matchPlayer);
            Format.broadcast(Settings.LEAVE_MESSAGE.replace("{player}", player.getName()));
        }
    }

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

    public MatchType getType() {
        return type;
    }
}
