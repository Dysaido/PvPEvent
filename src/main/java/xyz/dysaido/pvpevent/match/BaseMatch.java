package xyz.dysaido.pvpevent.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.api.event.MatchJoinEvent;
import xyz.dysaido.pvpevent.api.event.MatchLeaveEvent;
import xyz.dysaido.pvpevent.arena.Arena;
import xyz.dysaido.pvpevent.match.model.Participant;
import xyz.dysaido.pvpevent.setting.Settings;
import xyz.dysaido.pvpevent.util.Format;

import java.util.Objects;

import static xyz.dysaido.pvpevent.match.model.Participant.*;

public abstract class BaseMatch extends MatchTask {

    protected final Arena arena;
    protected final MatchQueue<Participant> queue;
    protected final MatchType type;
    protected volatile MatchState state = MatchState.WAITING;
    protected int round = 0;

    protected long lastTransaction = 0;

    protected int waiting = Settings.WAITING;
    protected int starting = Settings.STARTING;
    protected int ending = Settings.ENDING;

    public BaseMatch(PvPEvent plugin, Arena arena, MatchType type) {
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

    public synchronized void join(Player player) {
        Objects.requireNonNull(player);
        if (waiting > 0) {
            if (!queue.contains(player)) {
                Participant participant = new Participant(this, player);
                Bukkit.getServer().getPluginManager().callEvent(new MatchJoinEvent(this, player));
                queue.addParticipant(participant);
                Format.broadcast(Settings.JOIN_MESSAGE.replace("{player}", player.getName()));
            } else {
                player.sendMessage(Format.colored(Settings.ALREADY_JOINED_MESSAGE));
            }
        } else {
            player.sendMessage(Format.colored(Settings.EVENT_NOT_AVAILABLE_MESSAGE));
        }
    }

    public synchronized void leave(Player player) {
        Objects.requireNonNull(player);
        if (queue.contains(player)) {
            Participant participant = queue.findParticipantByName(player.getName());
            MatchLeaveEvent matchLeaveEvent = new MatchLeaveEvent(this, player);
            if (participant.getState().equals(State.FIGHT)) {
                matchLeaveEvent.setDuringFight(true);
                Bukkit.getServer().getPluginManager().callEvent(matchLeaveEvent);
                participant.reset(arena.getWorldSpawn(), true);
            } else {
                matchLeaveEvent.setDuringFight(false);
                Bukkit.getServer().getPluginManager().callEvent(matchLeaveEvent);
                participant.reset(arena.getWorldSpawn(), false);
            }
            queue.removeParticipant(participant);
            Format.broadcast(Settings.LEAVE_MESSAGE.replace("{player}", player.getName()));
        }
    }

    public abstract void loop();

    public abstract void nextRound();

    public abstract boolean hasFighting();

    public boolean shouldEnd() {
        return !queue.getParticipantsByState(State.QUEUE).findAny().isPresent();
    }

    public boolean shouldNextRound() {
        return getState().equals(MatchState.FIGHTING) && !shouldEnd();
    }

    public MatchQueue<Participant> getQueue() {
        return queue;
    }

    public Arena getArena() {
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
