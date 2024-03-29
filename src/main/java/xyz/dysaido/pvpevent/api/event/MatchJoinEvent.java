package xyz.dysaido.pvpevent.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;

public class MatchJoinEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final AbstractMatch match;
    private final Participant player;
    private final ParticipantStatus status;
    private boolean cancel;

    public MatchJoinEvent(AbstractMatch match, Participant player, ParticipantStatus status) {
        this.match = match;
        this.player = player;
        this.status = status;
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

    public Participant getPlayer() {
        return player;
    }

    public ParticipantStatus getStatus() {
        return status;
    }
}