package xyz.dysaido.onevsonegame.menu;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.menu.impl.EventsMenu;
import xyz.dysaido.onevsonegame.menu.impl.MainMenu;

public class MenuManager {

    private final MainMenu mainMenu;
    private final EventsMenu eventsMenu;

    public MenuManager(OneVSOneGame plugin) {
        this.mainMenu = new MainMenu(plugin);
        this.eventsMenu = new EventsMenu(plugin);
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public EventsMenu getEventsMenu() {
        return eventsMenu;
    }
}
