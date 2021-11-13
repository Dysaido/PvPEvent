package xyz.dysaido.onevsonegame.ring;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class Ring {
    private final String mName;
    private final Inventory mInventory;
    private final Location mSpawn;
    private final Location mLobby;
    private final Location mSpawn1;
    private final Location mSpawn2;


    public Ring(String name, Inventory inventory, Location worldSpawn, Location lobby, Location spawn1, Location spawn2) {
        this.mName = name;
        this.mInventory = inventory;
        this.mSpawn = worldSpawn;
        this.mLobby = lobby;
        this.mSpawn1 = spawn1;
        this.mSpawn2 = spawn2;
    }

    public String getName() {
        return mName;
    }

    public Location getWorldSpawn() {
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

    public Inventory getInventory() {
        return mInventory;
    }


}
