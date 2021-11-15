package xyz.dysaido.onevsonegame.ring;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.LocationSerializer;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RingManager {
    private final Map<String, Ring> arenaMap = new HashMap<>();
    private final static String TAG = "RingManager";
    private final FileManager ringConfig;
    public RingManager(FileManager fileManager) {
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
            RingCache cache = new RingCache(name);
            cache.setSpawn(LocationSerializer.deserialize(worldspawn));
            cache.setSpawn1(LocationSerializer.deserialize(spawn1));
            cache.setSpawn2(LocationSerializer.deserialize(spawn2));
            cache.setLobby(LocationSerializer.deserialize(lobby));
            cache.setContents(contents);
            cache.setArmor(armor);
            arenaMap.computeIfAbsent(name, s -> {
                Logger.debug("RingManager", cache.toString());
                return new Ring(cache);
            });
        }
    }

    public void save(RingCache ringCache) {
        FileConfiguration configuration = ringConfig.getFile();
        String name = ringCache.getName();
        String worldspawn = LocationSerializer.serialize(ringCache.getSpawn());
        String lobby = LocationSerializer.serialize(ringCache.getLobby());
        String spawn1 = LocationSerializer.serialize(ringCache.getSpawn1());
        String spawn2 = LocationSerializer.serialize(ringCache.getSpawn2());
        ItemStack[] contents = ringCache.getContents();
        ItemStack[] armor = ringCache.getArmor();
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

    public Ring get(String name) {
        load();
        return arenaMap.get(name);
    }

    public Ring add(Ring ring) {
        return arenaMap.computeIfPresent(ring.getName(),(s, internalRing) -> {
            Logger.warning(TAG, String.format("You can't make an ring that already exists (%s)", s));
            return internalRing;
        });
//        return arenaMap.putIfAbsent(ring.getName(), ring);
    }

    public Ring remove(Ring ring) {
        return remove(ring.getName());
    }

    public Ring remove(String name) {
        return arenaMap.remove(name);
    }

    public Collection<Ring> getRings() {
        return arenaMap.values();
    }

}
