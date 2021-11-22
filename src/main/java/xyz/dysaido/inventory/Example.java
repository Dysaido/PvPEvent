package xyz.dysaido.inventory;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class Example extends JavaPlugin {
    private static final String TITLE = "Test inventory";
    DyInventoryManager dyInventoryManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        dyInventoryManager  = new DyInventoryManager(this);
        DyInventory dyInventory = new DyInventory(TITLE, 6);
        dyInventory.setClickable(true);
        CustomAction<ItemStack, Player> action = new CustomAction<>(DyItemBuilder.create(Material.GOLDEN_APPLE, 1, "hallo"));
        action.addAction(player -> player.kickPlayer("Successfully"));
        for (int i = 0; i < 56; i++) {
            if (i % 5 == 0) dyInventory.addItem(i, action);
        }
        dyInventoryManager.addInventory(dyInventory);
        getServer().getPluginCommand("testinv").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Optional<DyInventory> optional = dyInventoryManager.findByTitle(TITLE);
            optional.ifPresent(dyInventory -> dyInventory.open((Player) sender));
        }
        return true;
    }


}
