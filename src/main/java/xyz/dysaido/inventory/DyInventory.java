package xyz.dysaido.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DyInventory {

    private final String title;
    private final Map<Integer, CustomAction<ItemStack, Player>> itemMap;
    private final Inventory inventory;
    private boolean clickable = false;

    public DyInventory(String title, int rows) {
        this.title = title;
        this.itemMap = new HashMap<>(rows * 9);
        this.inventory = Bukkit.createInventory(null, rows * 9, this.title);
    }

    public void open(Player player) {
        // replace inventory.getTitle() to inventory.getType().getDefaultTitle() for 1.17.1
        Inventory inner = Bukkit.createInventory(player, inventory.getSize(), inventory.getTitle());
        inner.setContents(inventory.getContents());
        player.openInventory(inner);
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (clickable) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (itemMap.containsKey(slot)) {
                itemMap.get(slot).getAction().ifPresent(consumer -> consumer.accept(player));
            }
        }
    }

    public void addItem(int i, CustomAction<ItemStack, Player> action) {
        itemMap.put(i, action);
        inventory.setItem(i, action.getMaterial());
    }

    public String getTitle() {
        return title;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

}
