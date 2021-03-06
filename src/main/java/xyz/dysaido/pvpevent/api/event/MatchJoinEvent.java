package xyz.dysaido.pvpevent.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.dysaido.pvpevent.match.BaseMatch;

public class MatchJoinEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final BaseMatch match;
    private final Player player;
    private boolean cancel;

    public MatchJoinEvent(BaseMatch match, Player player) {
        this.match = match;
        this.player = player;
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

    public BaseMatch getMatch() {
        return match;
    }

    public Player getPlayer() {
        return player;
    }
}