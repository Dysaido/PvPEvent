package xyz.dysaido.pvpevent.menu;

import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.inventory.BaseInventory;
import xyz.dysaido.pvpevent.inventory.button.Button;
import xyz.dysaido.pvpevent.inventory.row.InventoryRow;
import xyz.dysaido.pvpevent.arena.ArenaManager;
import xyz.dysaido.pvpevent.util.Format;
import xyz.dysaido.pvpevent.util.ItemBuilder;
import xyz.dysaido.pvpevent.util.Materials;

public class MenuManager {

    private final BaseInventory mainMenu;
    private final BaseInventory eventsMenu;
    private final PvPEvent plugin = PvPEvent.getInstance();
    private volatile boolean loaded = false;

    private static MenuManager instance;
    public static MenuManager getInstance() {
        return instance == null ? instance = new MenuManager() : instance;
    }

    private MenuManager() {
        this.mainMenu = InventoryRow.THREE.createInventory("&4&lEvent plugin");
        this.eventsMenu = InventoryRow.SIX.createInventory("&4&lManagement");
    }

    private void loadMainMenu() {
        this.mainMenu.setClickable(true);
        ItemBuilder info = new ItemBuilder(Materials.SIGN);
        info.displayName("&ePlugin Information");
        info.lore("&eDiscord: &7Dysaido#3162",
                String.format("&eVersion: &7%s", plugin.getDescription().getVersion())
        );
        ArenaManager arenaManager = plugin.getArenaManager();
        arenaManager.load();
        int size = arenaManager.getArenas().size();
        ItemBuilder events = new ItemBuilder(Materials.BOOK, size);
        events.displayName("&eEvents");
        events.lore("&7Click here, If you want to see events");
        ItemBuilder reload = new ItemBuilder(Materials.REDSTONE, 1);
        reload.displayName("&eReload the plugin");
        this.mainMenu.setButton(11, Button.of(events, player -> {
            player.sendMessage("The menu not supported yet.");
        }));
        this.mainMenu.setItem(13, info);
        this.mainMenu.setButton(15, Button.of(reload, player -> {
            plugin.reloadConfig();
            player.sendMessage(Format.colored("&aSuccess reload!"));
        }));
        loaded = true;
    }

    public BaseInventory getMainMenu() {
        if (!loaded) loadMainMenu();
        return mainMenu;
    }

    public BaseInventory getEventsMenu() {
        return eventsMenu;
    }
}
