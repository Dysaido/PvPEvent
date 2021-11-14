package xyz.dysaido.onevsonegame.ring;

import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;
import xyz.dysaido.onevsonegame.util.ItemSerializer;

public class RingCache {
    private final String mName;
    private String contents;
    private String armor;
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

    public void setArmor(String armor) {
        this.armor = armor;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public String getArmor() {
        return armor;
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
