package xyz.dysaido.onevsonegame.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.dysaido.onevsonegame.match.Match;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;

public class GamePlayerLoseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Match match;
    private final MatchPlayer player;
    private boolean cancel;

    public GamePlayerLoseEvent(Match match, MatchPlayer player) {
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

    public Match getMatch() {
        return match;
    }

    public MatchPlayer getPlayer() {
        return player;
    }
}
