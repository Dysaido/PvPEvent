package xyz.dysaido.pvpevent.api.event.impl;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.Participant;

public class DuelNextRoundEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final AbstractMatch match;
    private final Participant player1;
    private final Participant player2;
    private final int round;
    private boolean cancel;

    public DuelNextRoundEvent(AbstractMatch match, int round, Participant player1, Participant player2) {
        this.match = match;
        this.round = round;
        this.player1 = player1;
        this.player2 = player2;
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

    public Participant getPlayer1() {
        return player1;
    }

    public Participant getPlayer2() {
        return player2;
    }

    public int getRound() {
        return round;
    }
}