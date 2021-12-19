package xyz.dysaido.onevsonegame.util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Colorable;

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
        meta.setDisplayName(Format.colored(string));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.stream(lore).map(Format::colored).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(Collection<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore.stream().map(Format::colored).collect(Collectors.toList()));
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
