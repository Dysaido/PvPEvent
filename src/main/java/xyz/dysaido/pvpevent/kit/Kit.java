package xyz.dysaido.pvpevent.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {

    private final ItemStack[] contents;
    private final ItemStack[] armor;

    public Kit(ItemStack[] contents, ItemStack[] armor) {
        this.contents = contents;
        this.armor = armor;
    }

    public void apply(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.updateInventory();
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }
}
