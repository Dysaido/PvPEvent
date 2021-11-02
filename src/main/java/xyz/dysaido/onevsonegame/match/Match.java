package xyz.dysaido.onevsonegame.match;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class Match {

    private final Inventory mGameInventory;
    private final Location mSpawn;
    private final Location mLobby;
    private final Location mSpawn1;
    private final Location mSpawn2;

    public Match(Inventory inventory, Location spawn, Location lobby, Location spawn1, Location spawn2) {
        this.mGameInventory = inventory;
        this.mSpawn = spawn;
        this.mLobby = lobby;
        this.mSpawn1 = spawn1;
        this.mSpawn2 = spawn2;
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

    public Location getSpawn() {
        return mSpawn;
    }

    public Location getLobby() {
        return mLobby;
    }

    public Location getSpawn1() {
        return mSpawn1;
    }

    public Location getSpawn2() {
        return mSpawn2;
    }
}
