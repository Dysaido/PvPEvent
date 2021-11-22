package xyz.dysaido.onevsonegame.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.inventory.CustomAction;
import xyz.dysaido.inventory.DyInventory;
import xyz.dysaido.inventory.DyItemBuilder;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.util.Materials;

public class MainMenu extends DyInventory {

    private final OneVSOneGame plugin;
    private final ItemStack anvils = DyItemBuilder.create(Materials.ANVIL, 1, "&cEvents Manage");
    private final ItemStack books = DyItemBuilder.create(Materials.BOOK, 1, "&cContributor");
    private final ItemStack signs = DyItemBuilder.create(Materials.SIGN, 1, "&cAvailable Match");
    private final ItemStack hoes = DyItemBuilder.create(Materials.DIAMOND_HOE, 1, "&cArena Setter");

    public MainMenu(OneVSOneGame plugin) {
        super("Main menu", 3);
        this.plugin = plugin;
        setClickable(true);
        // 11,13,15

        CustomAction<ItemStack, Player> onEventsAction = new CustomAction<>(anvils);
        CustomAction<ItemStack, Player> onContributorsAction = new CustomAction<>(books);
        CustomAction<ItemStack, Player> onAvailableMatch = new CustomAction<>(signs);
        CustomAction<ItemStack, Player> onArenaSetterAction = new CustomAction<>(hoes);

        onEventsAction.addAction(player -> player.sendMessage("Saved event"));
        onContributorsAction.addAction(player -> player.sendMessage("Contributor"));
        onAvailableMatch.addAction(player -> player.sendMessage("onAvailableMatch"));
        onArenaSetterAction.addAction(player -> player.sendMessage("Arena Setter"));

        setItemWithAction(10, onEventsAction);
        setItemWithAction(13, onContributorsAction);
        setItemWithAction(15, onAvailableMatch);
        setItemWithAction(16, onArenaSetterAction);
    }


}
