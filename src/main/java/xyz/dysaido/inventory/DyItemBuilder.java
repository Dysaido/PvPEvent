package xyz.dysaido.inventory;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.List;

public class DyItemBuilder {

    public DyItemBuilder() {

    }

    public static ItemStack create(Material material, int amount, String name) {
        return create(material, amount, name, null);
    }

    public static ItemStack createItemWool(DyeColor dyeColor, int amount, String name) {
        return createItemWool(dyeColor, amount, name, null);
    }

    public static ItemStack create(Material material, int amount, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Format.colored(name));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createItemWool(DyeColor dyeColor, int amount, String name, List<String> lore) {
        Wool wool = new Wool(dyeColor);
        ItemStack itemStack = wool.toItemStack(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Format.colored(name));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
