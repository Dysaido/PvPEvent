/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.dysaido.pvpevent.api.pagination;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import xyz.dysaido.pvpevent.api.pagination.button.Button;
import xyz.dysaido.pvpevent.util.Logger;

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

    @SuppressWarnings("unchecked")
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
            Logger.warn(TAG, "Overflow prevented");
            return;
        }
        setItem(coordinate, button.getItem());
        buttonMap.put(coordinate, button);
    }

    public void setItem(int coordinate, ItemBuilder item) {
        Objects.requireNonNull(item);
        if (coordinate > Math.min(coordinate, Math.min(row, 6) * 9 - 1)) {
            Logger.warn(TAG, "Overflow prevented");
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
