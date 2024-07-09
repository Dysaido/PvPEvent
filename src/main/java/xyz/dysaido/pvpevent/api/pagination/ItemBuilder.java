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

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Colorable;
import xyz.dysaido.pvpevent.util.BukkitHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, short damage) {
        this.item = new ItemStack(material, 1, damage);
    }

    public ItemBuilder(Material material, int amount, short damage) {
        this.item = new ItemStack(material, amount, damage);
    }

    public ItemBuilder(Material material, int amount, short damage, Byte data) {
        this.item = new ItemStack(material, amount, damage, data);
    }

    public ItemBuilder displayName(String string) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(BukkitHelper.colorize(string));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.stream(lore).map(BukkitHelper::colorize).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(Collection<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore.stream().map(BukkitHelper::colorize).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder color(Color color) {
        if (item instanceof Colorable) {
            ((Colorable) item).setColor(DyeColor.getByColor(color));
        } else if (item.hasItemMeta() && item.getItemMeta() instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) item.getItemMeta()).setColor(color);
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        item.removeEnchantment(enchantment);
        return this;
    }

    public ItemStack build() {
        return item;
    }
}
