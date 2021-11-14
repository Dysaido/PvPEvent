package xyz.dysaido.onevsonegame.ring;

import org.bukkit.Location;
import xyz.dysaido.onevsonegame.kit.Kit;

public class Ring {
    private final String mName;
    private final Kit mKit;
    private final Location mSpawn;
    private final Location mLobby;
    private final Location mSpawn1;
    private final Location mSpawn2;

    public Ring(String name, Kit kit, Location worldSpawn, Location lobby, Location spawn1, Location spawn2) {
        this.mName = name;
        this.mKit = kit;
        this.mSpawn = worldSpawn;
        this.mLobby = lobby;
        this.mSpawn1 = spawn1;
        this.mSpawn2 = spawn2;
    }

    public Ring(RingCache ringCache) {
        this.mName = ringCache.getName();
        this.mKit = new Kit(ringCache.getContents(), ringCache.getArmor());
        this.mSpawn = ringCache.getSpawn();
        this.mLobby = ringCache.getLobby();
        this.mSpawn1 = ringCache.getSpawn1();
        this.mSpawn2 = ringCache.getSpawn2();
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

    public Kit getKit() {
        return mKit;
    }


}
