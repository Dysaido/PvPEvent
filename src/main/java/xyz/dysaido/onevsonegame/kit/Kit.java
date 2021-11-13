package xyz.dysaido.onevsonegame.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Kit {

    private final ItemStack[] contents = new ItemStack[36];
    private final ItemStack[] armor = new ItemStack[4];

    public void apply(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.updateInventory();
    }

}
