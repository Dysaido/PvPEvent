package xyz.dysaido.onevsonegame.arena;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.LocationSerializer;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    private final Map<String, Arena> arenaMap = new HashMap<>();
    private final static String TAG = "ArenaManager";
    private final FileManager ringConfig;
    private boolean loaded = false;
    public ArenaManager(FileManager fileManager) {
        this.ringConfig = fileManager;
    }

    // TODO: load from config some arenas
    public void load() {
        FileConfiguration configuration = ringConfig.getFile();
        for (String name : configuration.getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection(name);
            String worldspawn = section.getString("worldspawn");
            String lobby = section.getString("lobby");
            String spawn1 = section.getString("spawn1");
            String spawn2 = section.getString("spawn2");
            ItemStack[] contents = section.getList("contents").toArray(new ItemStack[0]);
            ItemStack[] armor = section.getList("armor").toArray(new ItemStack[0]);
            ArenaCache cache = new ArenaCache(name);
            cache.setSpawn(LocationSerializer.deserialize(worldspawn));
            cache.setSpawn1(LocationSerializer.deserialize(spawn1));
            cache.setSpawn2(LocationSerializer.deserialize(spawn2));
            cache.setLobby(LocationSerializer.deserialize(lobby));
            cache.setContents(contents);
            cache.setArmor(armor);
            arenaMap.computeIfAbsent(name, s -> {
                Logger.debug("RingManager", cache.toString());
                return new Arena(cache);
            });
        }
        loaded = true;
    }

    public void save(ArenaCache arenaCache) {
        FileConfiguration configuration = ringConfig.getFile();
        String name = arenaCache.getName();
        String worldspawn = LocationSerializer.serialize(arenaCache.getSpawn());
        String lobby = LocationSerializer.serialize(arenaCache.getLobby());
        String spawn1 = LocationSerializer.serialize(arenaCache.getSpawn1());
        String spawn2 = LocationSerializer.serialize(arenaCache.getSpawn2());
        ItemStack[] contents = arenaCache.getContents();
        ItemStack[] armor = arenaCache.getArmor();
        if (!configuration.isConfigurationSection(name)) {
            ConfigurationSection section = configuration.createSection(name);
            section.set("worldspawn", worldspawn);
            section.set("lobby", lobby);
            section.set("spawn1", spawn1);
            section.set("spawn2", spawn2);
            section.set("contents", contents);
            section.set("armor", armor);
        }
        ringConfig.saveFile();
        ringConfig.reloadFile();
        load();
    }

    public Arena get(String name) {
        if (!loaded) load();
        return arenaMap.get(name);
    }

    public Arena add(Arena arena) {
        return arenaMap.computeIfPresent(arena.getName(),(s, internalRing) -> {
            Logger.warning(TAG, String.format("You can't make an ring that already exists (%s)", s));
            return internalRing;
        });
//        return arenaMap.putIfAbsent(ring.getName(), ring);
    }

    public Arena remove(Arena arena) {
        return remove(arena.getName());
    }

    public Arena remove(String name) {
        FileConfiguration configuration = ringConfig.getFile();
        if (configuration.isConfigurationSection(name)) {
            configuration.set(name, null);
        }
        ringConfig.saveFile();
        return arenaMap.remove(name);
    }

    public Collection<Arena> getArenas() {
        if (!loaded) load();
        return arenaMap.values();
    }

}
