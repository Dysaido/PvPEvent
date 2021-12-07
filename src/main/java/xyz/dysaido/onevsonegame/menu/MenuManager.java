package xyz.dysaido.onevsonegame.menu;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.inventory.BaseInventory;
import xyz.dysaido.onevsonegame.inventory.row.InventoryRow;

public class MenuManager {

    private final BaseInventory mainMenu;
    private final BaseInventory eventsMenu;

    public MenuManager(OneVSOneGame plugin) {
        this.mainMenu = InventoryRow.SIX.createInventory("&4&lEvent plugin");
        this.eventsMenu = InventoryRow.SIX.createInventory("&4&lManagement");
    }

    public BaseInventory getMainMenu() {
        return mainMenu;
    }

    public BaseInventory getEventsMenu() {
        return eventsMenu;
    }
}
