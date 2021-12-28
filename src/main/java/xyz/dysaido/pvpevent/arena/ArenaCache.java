package xyz.dysaido.pvpevent.arena;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ArenaCache {
    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private Location spawn;
    private Location lobby;
    private Location spawn1;
    private Location spawn2;

    public ArenaCache(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location mSpawn) {
        this.spawn = mSpawn;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location mLobby) {
        this.lobby = mLobby;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public void setSpawn1(Location mSpawn1) {
        this.spawn1 = mSpawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public void setSpawn2(Location mSpawn2) {
        this.spawn2 = mSpawn2;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "ArenaCache{" +
                "name='" + name + '\'' +
                ", contents=" + Arrays.toString(contents) +
                ", armor=" + Arrays.toString(armor) +
                ", spawn=" + spawn +
                ", lobby=" + lobby +
                ", spawn1=" + spawn1 +
                ", spawn2=" + spawn2 +
                '}';
    }
}
