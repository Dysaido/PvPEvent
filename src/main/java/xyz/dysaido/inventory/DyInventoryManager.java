package xyz.dysaido.inventory;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DyInventoryManager {

    private final Set<DyInventory> inventories = new HashSet<>();

    public DyInventoryManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new DyListener(this), plugin);
    }

    public Optional<DyInventory> findByTitle(String title) {
        DyInventory inventory = inventories.stream().filter(dyInventory -> dyInventory.getTitle().equals(title)).findFirst().orElse(null);
        return Optional.ofNullable(inventory);
    }

    public void addInventory(DyInventory dyInventory) {
        inventories.add(dyInventory);
    }

    public void removeInventory(DyInventory dyInventory) {
        inventories.remove(dyInventory);
    }

    public Set<DyInventory> getInventories() {
        return Collections.unmodifiableSet(inventories);
    }

}
