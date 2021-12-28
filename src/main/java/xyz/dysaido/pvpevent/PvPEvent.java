package xyz.dysaido.pvpevent;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.command.CommandDispatcher;
import xyz.dysaido.pvpevent.inventory.listener.InventoryListener;
import xyz.dysaido.pvpevent.listener.MatchListener;
import xyz.dysaido.pvpevent.match.MatchHandler;
import xyz.dysaido.pvpevent.arena.ArenaManager;
import xyz.dysaido.pvpevent.api.config.Config;
import xyz.dysaido.pvpevent.setting.Settings;
import xyz.dysaido.pvpevent.util.FileManager;
import xyz.dysaido.pvpevent.util.Logger;
import xyz.dysaido.pvpevent.util.Reflection;
import xyz.dysaido.pvpevent.util.ServerVersion;

import java.lang.reflect.Field;

public final class PvPEvent extends JavaPlugin {
    private static final String TAG = "MAIN";
    private static PvPEvent instance;
    private FileManager ringConfig;
    private ArenaManager arenaManager;
    private MatchHandler matchHandler;
    private Config config;
    private ServerVersion serverVersion;

    public static PvPEvent getInstance() {
        synchronized (PvPEvent.class) {
            return instance;
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        serverVersion = ServerVersion.valueOf(Reflection.VERSION);
        ringConfig = new FileManager(this, "rings");
        config = new Config(this);
        config.initialAnnotatedClass(Settings.class);
        arenaManager = new ArenaManager(ringConfig);
        matchHandler = new MatchHandler(this);

        CommandDispatcher.enable(this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new MatchListener(this), this);
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
        CommandDispatcher.getInstance().unregisterAll();
        Logger.information(TAG, "PvPEvent plugin was disabled");
    }

    public SimpleCommandMap getCommandMap() {
        Field field = Reflection.getField(getServer().getClass(), SimpleCommandMap.class);
        return Reflection.fetch(getServer(), field);
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public MatchHandler getMatchHandler() {
        return matchHandler;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

}
