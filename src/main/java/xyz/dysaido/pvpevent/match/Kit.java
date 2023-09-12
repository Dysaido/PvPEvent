package xyz.dysaido.pvpevent.match;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Kit<T extends Player> implements Consumer<T> {

    public static final Kit<Player> EMPTY = new Kit<>("NULL");

    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;

    public Kit(String name) {
        this(name, null, null);
    }

    public Kit(String name, ItemStack[] contents, ItemStack[] armor) {
        this.name = name;
        this.contents = contents;
        this.armor = armor;
    }

    @Override
    public void accept(T player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");
        if (contents != null) {
            player.getInventory().setContents(contents);
        }
        player.getInventory().setArmorContents(armor);
        player.updateInventory();
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

}
