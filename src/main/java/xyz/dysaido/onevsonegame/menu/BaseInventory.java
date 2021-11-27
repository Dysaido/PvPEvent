package xyz.dysaido.onevsonegame.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseInventory implements InventoryHolder {

    private final Map<Integer, ActionPair<ItemStack, Player>> itemMap;
    private final Inventory inventory;
    private boolean clickable = false;

    public BaseInventory(String title, int rows) {
        this.itemMap = new HashMap<>(rows * 9);
        this.inventory = Bukkit.createInventory(this, rows * 9, Format.colored(title));
    }

    public void open(Player player) {
        if (inventory != null) {
            player.openInventory(this.inventory);
        }
    }

    public void onClick(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            Player player = (Player) whoClicked;
            if (clickable) {
                int slot = event.getSlot();
                if (itemMap.containsKey(slot)) {
                    itemMap.get(slot).getAction().ifPresent(consumer -> consumer.accept(player));
                }
            }
        }
    }

    public void setItemWithAction(int i, ActionPair<ItemStack, Player> action) {
        itemMap.put(i, action);
        inventory.setItem(i, action.getKey());
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
