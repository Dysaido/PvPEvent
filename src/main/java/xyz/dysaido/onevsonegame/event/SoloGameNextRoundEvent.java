package xyz.dysaido.onevsonegame.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.util.Pair;

public class SoloGameNextRoundEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final BaseMatch match;
    private final Pair<MatchPlayer, MatchPlayer> opponents;
    private boolean cancel;

    public SoloGameNextRoundEvent(BaseMatch match, Pair<MatchPlayer, MatchPlayer> opponents) {
        this.match = match;
        this.opponents = opponents;
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

    public Pair<MatchPlayer, MatchPlayer> getOpponents() {
        return opponents;
    }
}
