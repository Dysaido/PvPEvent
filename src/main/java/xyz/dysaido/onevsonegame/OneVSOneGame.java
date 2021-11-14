package xyz.dysaido.onevsonegame;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.command.impl.EventCommand;
import xyz.dysaido.onevsonegame.listener.MatchListener;
import xyz.dysaido.onevsonegame.match.MatchManager;
import xyz.dysaido.onevsonegame.ring.RingManager;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.Reflection;

import java.lang.reflect.Field;

public final class OneVSOneGame extends JavaPlugin {

    private static OneVSOneGame instance;
    private SimpleCommandMap simpleCommandMap;
    private FileManager ringConfig;
    private RingManager ringManager;
    private MatchManager matchManager;

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

        getCommandMap().register("event", new EventCommand(this));
        getServer().getPluginManager().registerEvents(new MatchListener(this), this);
    }

    @Override
    public void onDisable() {
        instance = null;

    }

    public SimpleCommandMap getCommandMap() {
        if (simpleCommandMap == null) {
            Field field = Reflection.getField(getServer().getClass(), "commandMap");
            simpleCommandMap = Reflection.getObject(getServer(), field, SimpleCommandMap.class);
        }
        return simpleCommandMap;
    }

    public RingManager getRingManager() {
        return ringManager;
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

}
