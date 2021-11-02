package xyz.dysaido.onevsonegame.arena;

import org.bukkit.Location;

public class Arena {
    private final String mName;
    private final Location mSpawn;
    private final Location mLobby;
    private final Location mSpawn1;
    private final Location mSpawn2;


    public Arena(String name, Location spawn, Location lobby, Location spawn1, Location spawn2) {
        this.mName = name;
        this.mSpawn = spawn;
        this.mLobby = lobby;
        this.mSpawn1 = spawn1;
        this.mSpawn2 = spawn2;
    }

    public String getName() {
        return mName;
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
