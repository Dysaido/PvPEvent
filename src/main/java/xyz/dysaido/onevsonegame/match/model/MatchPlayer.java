package xyz.dysaido.onevsonegame.match.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.dysaido.onevsonegame.match.Match;
import xyz.dysaido.onevsonegame.match.MatchState;

public class MatchPlayer {

    private MatchState state = MatchState.QUEUE;
    private final Inventory mOriginalInventory;
    private final Inventory mGameInventory;

    public MatchPlayer(Match match, Player player) {
        mOriginalInventory = player.getInventory();
        mGameInventory = match.getGameInventory();
    }

    public Inventory getOriginalInventory() {
        return mOriginalInventory;
    }

    public Inventory getGameInventory() {
        return mGameInventory;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }
}
