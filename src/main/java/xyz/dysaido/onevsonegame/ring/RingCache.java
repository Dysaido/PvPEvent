package xyz.dysaido.onevsonegame.ring;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class RingCache {
    private final String mName;
    private Inventory mInventory;
    private Location mSpawn;
    private Location mLobby;
    private Location mSpawn1;
    private Location mSpawn2;

    public RingCache(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public Inventory getInventory() {
        return mInventory;
    }

    public void setInventory(Inventory mInventory) {
        this.mInventory = mInventory;
    }

    public Location getSpawn() {
        return mSpawn;
    }

    public void setSpawn(Location mSpawn) {
        this.mSpawn = mSpawn;
    }

    public Location getLobby() {
        return mLobby;
    }

    public void setLobby(Location mLobby) {
        this.mLobby = mLobby;
    }

    public Location getSpawn1() {
        return mSpawn1;
    }

    public void setSpawn1(Location mSpawn1) {
        this.mSpawn1 = mSpawn1;
    }

    public Location getSpawn2() {
        return mSpawn2;
    }

    public void setSpawn2(Location mSpawn2) {
        this.mSpawn2 = mSpawn2;
    }
}