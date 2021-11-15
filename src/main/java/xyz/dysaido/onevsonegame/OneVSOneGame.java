package xyz.dysaido.onevsonegame;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.command.impl.EventCommand;
import xyz.dysaido.onevsonegame.listener.MatchListener;
import xyz.dysaido.onevsonegame.match.MatchManager;
import xyz.dysaido.onevsonegame.ring.RingManager;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.Reflection;

import java.lang.reflect.Field;
import java.util.Map;

public final class OneVSOneGame extends JavaPlugin {

    private static OneVSOneGame instance;
    private SimpleCommandMap simpleCommandMap;
    private Map<String, Command> knownCommands;
    private FileManager ringConfig;
    private RingManager ringManager;
    private MatchManager matchManager;
    private EventCommand eventCommand;

    public static OneVSOneGame getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        ringConfig = new FileManager(this, "rings");
        getConfig().options().copyDefaults(true);
        saveConfig();
        ringManager = new RingManager(ringConfig);
        matchManager = new MatchManager(this);
        eventCommand = new EventCommand(this);
        Bukkit.getScheduler().runTaskLater(this, () -> ringManager.load(), 200);
        getServer().getPluginManager().registerEvents(new MatchListener(this), this);
        registerCommand();
    }

    private void registerCommand() {
        getCommandMap().register("event", eventCommand);
    }

    private void unregisterCommand() {
        knownCommands.values().remove(eventCommand);
        eventCommand.unregister(simpleCommandMap);
    }

    @Override
    public void onDisable() {
        instance = null;
        unregisterCommand();
    }

    public SimpleCommandMap getCommandMap() {
        if (simpleCommandMap == null) {
            Field field = Reflection.getField(getServer().getClass(), "commandMap");
            simpleCommandMap = Reflection.getObject(getServer(), field, SimpleCommandMap.class);
        }
        return simpleCommandMap;
    }

    public Map<String, Command> getKnownCommands(SimpleCommandMap simpleCommandMap) {
        if (knownCommands == null) {
            Field field = Reflection.getField(SimpleCommandMap.class, "knownCommands");
            knownCommands = Reflection.getObject(simpleCommandMap, field, Map.class);
        }
        return knownCommands;
    }

    public RingManager getRingManager() {
        return ringManager;
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

}
