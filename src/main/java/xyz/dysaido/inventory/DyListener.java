package xyz.dysaido.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;
import java.util.Optional;

public class DyListener implements Listener {

    private final DyInventoryManager inventoryManager;

    public DyListener(DyInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            Player player = (Player) humanEntity;
            Inventory inventory = event.getClickedInventory();
            if (Objects.nonNull(inventory) && !inventory.equals(player.getInventory())) {
                // replace inventory.getTitle() to inventory.getType().getDefaultTitle() for 1.17.1
                Optional<DyInventory> optional = inventoryManager.findByTitle(event.getView().getTitle());
                optional.ifPresent(dyInventory -> dyInventory.onClick(event));
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

    }
}
