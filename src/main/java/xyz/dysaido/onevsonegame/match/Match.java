package xyz.dysaido.onevsonegame.match;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import xyz.dysaido.onevsonegame.arena.Arena;

public class Match {

    private final Inventory mGameInventory;
    private final Arena mArena;

    public Match(Inventory inventory, Arena arena) {
        this.mGameInventory = inventory;
        this.mArena = arena;
    }

    public void init() {

    }

    public void start() {

    }

    public void end() {

    }

    public Inventory getGameInventory() {
        return mGameInventory;
    }

    public Arena getArena() {
        return mArena;
    }
}
