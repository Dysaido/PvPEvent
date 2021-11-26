package xyz.dysaido.onevsonegame.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.menu.ActionPair;
import xyz.dysaido.onevsonegame.menu.BaseInventory;
import xyz.dysaido.onevsonegame.util.ItemBuilder;
import xyz.dysaido.onevsonegame.util.Materials;

public class MainMenu extends BaseInventory {

    private final OneVSOneGame plugin;
    private final ItemStack anvils = new ItemBuilder(Materials.ANVIL, 1).displayName("&cEvents Manage").build();
    private final ItemStack books = new ItemBuilder(Materials.BOOK, 1).displayName("&cContributor").build();
    private final ItemStack signs = new ItemBuilder(Materials.SIGN, 1).displayName("&cAvailable Match").build();
    private final ItemStack hoes = new ItemBuilder(Materials.DIAMOND_HOE, 1).displayName("&cArena Setter").build();

    public MainMenu(OneVSOneGame plugin) {
        super("Main menu", 3);
        this.plugin = plugin;
        setClickable(true);
        // 11,13,15

        ActionPair<ItemStack, Player> onEventsAction = new ActionPair<>(anvils);
        ActionPair<ItemStack, Player> onContributorsAction = new ActionPair<>(books);
        ActionPair<ItemStack, Player> onAvailableMatch = new ActionPair<>(signs);
        ActionPair<ItemStack, Player> onArenaSetterAction = new ActionPair<>(hoes);

        onEventsAction.setAction(player -> player.sendMessage("Saved event"));
        onContributorsAction.setAction(player -> player.sendMessage("Contributor"));
        onAvailableMatch.setAction(player -> player.sendMessage("onAvailableMatch"));
        onArenaSetterAction.setAction(player -> player.sendMessage("Arena Setter"));

        setItemWithAction(10, onEventsAction);
        setItemWithAction(13, onContributorsAction);
        setItemWithAction(15, onAvailableMatch);
        setItemWithAction(16, onArenaSetterAction);
    }


}
