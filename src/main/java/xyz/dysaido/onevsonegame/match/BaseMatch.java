package xyz.dysaido.onevsonegame.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.api.MatchJoinEvent;
import xyz.dysaido.onevsonegame.api.MatchLeaveEvent;
import xyz.dysaido.onevsonegame.arena.Arena;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.Objects;

public abstract class BaseMatch extends MatchTask {

    protected final Arena arena;
    protected final MatchQueue<MatchPlayer> queue;
    protected final MatchType type;
    protected volatile MatchState state = MatchState.WAITING;
    protected int round = 0;

    protected long lastTransaction = 0;

    protected int waiting = Settings.WAITING;
    protected int starting = Settings.STARTING;
    protected int ending = Settings.ENDING;

    public BaseMatch(OneVSOneGame plugin, Arena arena, MatchType type) {
        super(arena.getName(), plugin);
        this.arena = arena;
        this.type = type;
        this.queue = new MatchQueue<>(this);
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
            if (!queue.containsQueue(player)) {
                MatchPlayer matchPlayer = new MatchPlayer(this, player);
                Bukkit.getServer().getPluginManager().callEvent(new MatchJoinEvent(this, player));
                queue.addQueue(matchPlayer);
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
        if (queue.containsQueue(player)) {
            MatchPlayer matchPlayer = queue.findQueueByName(player.getName());
            matchPlayer.reset(arena.getWorldSpawn(), false);

            MatchLeaveEvent matchLeaveEvent = new MatchLeaveEvent(this, player);
            matchLeaveEvent.setDuringFight(false);
            Bukkit.getServer().getPluginManager().callEvent(matchLeaveEvent);

            queue.removeQueue(matchPlayer);
            Format.broadcast(Settings.LEAVE_MESSAGE.replace("{player}", player.getName()));
        } else if (queue.containsFighter(player)) {
            MatchPlayer matchPlayer = queue.findFighterByName(player.getName());
            matchPlayer.reset(arena.getWorldSpawn(), true);

            MatchLeaveEvent matchLeaveEvent = new MatchLeaveEvent(this, player);
            matchLeaveEvent.setDuringFight(true);
            Bukkit.getServer().getPluginManager().callEvent(matchLeaveEvent);

            queue.removeFighter(matchPlayer);
            Format.broadcast(Settings.LEAVE_MESSAGE.replace("{player}", player.getName()));
        }
    }

    public abstract void loop();

    public abstract void nextRound();

    public abstract boolean hasFighting();

    public abstract boolean shouldEnd();

    public boolean shouldNextRound() {
        return queue.getPendingQueue().size() > 0 && getState().equals(MatchState.FIGHTING) && !shouldEnd();
    }

    public MatchQueue<MatchPlayer> getQueue() {
        return queue;
    }

    public Arena getRing() {
        return arena;
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
