package xyz.dysaido.onevsonegame;

import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.Reflection;

import java.lang.reflect.Field;

public final class OneVSOneGame extends JavaPlugin {

    private static OneVSOneGame instance;
    private FileManager arenas;

    public static OneVSOneGame getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        arenas = new FileManager(this, "games");
        getConfig().options().copyDefaults(true);
        saveConfig();

    }

    @Override
    public void onDisable() {
        instance = null;

    }

    public SimpleCommandMap getCommandMap() {
        Server server = getServer();
        Field field = Reflection.getField(server.getClass(), "commandMap");
        return Reflection.getObject(server, field, SimpleCommandMap.class);
    }

    public FileManager getArenas() {
        return arenas;
    }
}
