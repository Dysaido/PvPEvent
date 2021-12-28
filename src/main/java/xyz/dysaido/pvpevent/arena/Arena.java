package xyz.dysaido.pvpevent.arena;

import org.bukkit.Location;
import xyz.dysaido.pvpevent.kit.Kit;

public class Arena {
    private final String name;
    private final Kit kit;
    private final Location spawn;
    private final Location lobby;
    private final Location spawn1;
    private final Location spawn2;

    public Arena(String name, Kit kit, Location worldSpawn, Location lobby, Location spawn1, Location spawn2) {
        this.name = name;
        this.kit = kit;
        this.spawn = worldSpawn;
        this.lobby = lobby;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
    }

    public Arena(ArenaCache arenaCache) {
        this.name = arenaCache.getName();
        this.kit = new Kit(arenaCache.getContents(), arenaCache.getArmor());
        this.spawn = arenaCache.getSpawn();
        this.lobby = arenaCache.getLobby();
        this.spawn1 = arenaCache.getSpawn1();
        this.spawn2 = arenaCache.getSpawn2();
    }

    public String getName() {
        return name;
    }

    public Location getWorldSpawn() {
        return spawn;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public Kit getKit() {
        return kit;
    }

}
