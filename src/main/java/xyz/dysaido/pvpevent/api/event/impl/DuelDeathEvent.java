package xyz.dysaido.pvpevent.api.event.impl;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.Participant;

import javax.annotation.Nullable;

public class DuelDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final AbstractMatch match;
    private final Participant victim;
    private final Participant killer;
    private final int round;
    private final String message;
    private boolean cancel;

    public DuelDeathEvent(AbstractMatch match, int round, Participant victim, Participant killer, String message) {
        this.match = match;
        this.round = round;
        this.victim = victim;
        this.killer = killer;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public AbstractMatch getMatch() {
        return match;
    }

    public Participant getVictim() {
        return victim;
    }

    @Nullable
    public Participant getKiller() {
        return killer;
    }

    public String getMessage() {
        return message;
    }

    public int getRound() {
        return round;
    }
}