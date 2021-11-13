package xyz.dysaido.onevsonegame.match.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.dysaido.onevsonegame.match.Match;

import java.util.UUID;

public class MatchPlayer {

    private final Inventory mOriginalInventory;
    private final Inventory mRingInventory;
    private final Player mPlayer;
    private final Match mMatch;
    private PlayerState state = PlayerState.QUEUE;
    public MatchPlayer(Match match, Player player) {
        mMatch = match;
        mPlayer = player;
        mOriginalInventory = player.getInventory();
        mRingInventory = match.getRing().getInventory();
    }

    public void setup() {

    }

    public void reset() {

    }

    public UUID id() {
        return mPlayer.getUniqueId();
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Inventory getOriginalInventory() {
        return mOriginalInventory;
    }

    public Inventory getRingInventory() {
        return mRingInventory;
    }

    public Match getMatch() {
        return mMatch;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }
}
