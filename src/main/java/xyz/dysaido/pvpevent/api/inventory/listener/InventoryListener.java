package xyz.dysaido.pvpevent.api.inventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import xyz.dysaido.pvpevent.api.inventory.BaseInventory;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryAction(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory() != null ? event.getClickedInventory() : event.getInventory();
        if (inventory != null && inventory.getHolder() instanceof BaseInventory) {
            ((BaseInventory) inventory.getHolder()).onClick(event);
            event.setCancelled(true);
        } else if (event.getView().getTopInventory().getHolder() instanceof BaseInventory) {
            event.setCancelled(true);
        }
    }

}
