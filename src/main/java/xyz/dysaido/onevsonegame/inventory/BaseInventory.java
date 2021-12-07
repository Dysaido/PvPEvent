package xyz.dysaido.onevsonegame.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import xyz.dysaido.onevsonegame.inventory.button.Button;
import xyz.dysaido.onevsonegame.util.ItemBuilder;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BaseInventory implements InventoryHolder {
    private static final String TAG = "BaseInventory";
    private final String title;
    private final Inventory inventory;
    private final Map<Integer, Button> buttonMap = new HashMap<>();
    private final int row;
    private boolean clickable = false;

    public BaseInventory(String title, int row) {
        Objects.requireNonNull(title);
        this.title = ChatColor.translateAlternateColorCodes('&', title.substring(0, Math.min(title.length(), 32)));
        this.inventory = Bukkit.createInventory(this, Math.min(row, 6) * 9, this.title);
        this.row = row;
    }

    public static <Param extends BaseInventory> Param getCurrent(Player player) {
        Objects.requireNonNull(player);
        InventoryView view = player.getOpenInventory();
        if (view.getTopInventory() instanceof BaseInventory) {
            return (Param) view.getTopInventory();
        }
        return null;
    }

    public void open(Player player) {
        Objects.requireNonNull(player);
        player.openInventory(this.inventory);
    }

    public void openAll() {
        Bukkit.getOnlinePlayers().forEach(this::open);
    }

    public void closeAll() {
        this.inventory.getViewers().forEach(HumanEntity::closeInventory);
    }

    public void onClick(InventoryClickEvent event) {
        Objects.requireNonNull(event);
        if (!clickable) return;
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            int slot = event.getSlot();
            if (buttonMap.containsKey(slot)) {
                Player player = (Player) whoClicked;
                buttonMap.get(slot).getAction().accept(player);
            }
        }
    }

    public void setButton(int coordinate, Button button) {
        Objects.requireNonNull(button);
        if (coordinate > Math.min(coordinate, Math.min(row, 6) * 9 - 1)) {
            Logger.warning(TAG, "Overflow prevented");
            return;
        }
        setItem(coordinate, button.getItem());
        buttonMap.put(coordinate, button);
    }

    public void setItem(int coordinate, ItemBuilder item) {
        Objects.requireNonNull(item);
        if (coordinate > Math.min(coordinate, Math.min(row, 6) * 9 - 1)) {
            Logger.warning(TAG, "Overflow prevented");
            return;
        }
        this.inventory.setItem(coordinate, item.build());
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
