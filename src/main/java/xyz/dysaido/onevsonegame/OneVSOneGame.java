package xyz.dysaido.onevsonegame;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.command.impl.EventCommand;
import xyz.dysaido.onevsonegame.listener.MatchListener;
import xyz.dysaido.onevsonegame.match.MatchManager;
import xyz.dysaido.onevsonegame.menu.impl.MainMenu;
import xyz.dysaido.onevsonegame.menu.MenuManager;
import xyz.dysaido.onevsonegame.ring.RingManager;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.setting.Config;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.Logger;
import xyz.dysaido.onevsonegame.util.Reflection;
import xyz.dysaido.onevsonegame.util.ServerVersion;

import java.lang.reflect.Field;
import java.util.Map;

public final class OneVSOneGame extends JavaPlugin {
    private static final String TAG = "MAIN";
    private static OneVSOneGame instance;
    private FileManager ringConfig;
    private RingManager ringManager;
    private MatchManager matchManager;
    private EventCommand eventCommand;
    private Config config;
    private ServerVersion serverVersion;
    private MainMenu mainMenu;
    private MenuManager menuManager;
    public static OneVSOneGame getInstance() {
        synchronized (OneVSOneGame.class) {
            return instance;
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        serverVersion = ServerVersion.valueOf(Reflection.VERSION);
        Logger.information(TAG, "1v1Event plugin was enabled");
        Logger.information(TAG, "Contributing: https://github.com/Dysaido/1v1Event");
        Logger.information(TAG, "Check for updates: https://www.spigotmc.org/resources/1v1-event.97786/");
        menuManager = new MenuManager(this);
        ringConfig = new FileManager(this, "rings");
        config = new Config(this);
        config.initialAnnotatedClass(Settings.class);
        ringManager = new RingManager(ringConfig);
        matchManager = new MatchManager(this);
        eventCommand = new EventCommand(this);
        eventCommand.loadCommands();
        getServer().getPluginManager().registerEvents(new MatchListener(this), this);
        registerCommand();
    }

    private void registerCommand() {
        getCommandMap().register("event", eventCommand);
        getKnownCommands(getCommandMap()).put(eventCommand.getName(), eventCommand);
    }

    private void unregisterCommand() {
        getKnownCommands(getCommandMap()).values().remove(eventCommand);
        eventCommand.unregister(getCommandMap());
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        config.initialAnnotatedClass(Settings.class);
        ringConfig.reloadFile();
    }

    @Override
    public void onDisable() {
        instance = null;
        Logger.information(TAG, "1v1Event plugin was disabled");
        unregisterCommand();
    }

    public SimpleCommandMap getCommandMap() {
        Field field = Reflection.getField(getServer().getClass(), "commandMap");
        return Reflection.getObject(getServer(), field, SimpleCommandMap.class);
    }

    public Map<String, Command> getKnownCommands(SimpleCommandMap simpleCommandMap) {
        Field field = Reflection.getField(SimpleCommandMap.class, "knownCommands");
        return Reflection.getObject(simpleCommandMap, field, Map.class);
    }

    public RingManager getRingManager() {
        return ringManager;
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}
