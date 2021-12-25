package xyz.dysaido.onevsonegame;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.command.CommandDispatcher;
import xyz.dysaido.onevsonegame.inventory.listener.InventoryListener;
import xyz.dysaido.onevsonegame.listener.MatchListener;
import xyz.dysaido.onevsonegame.match.MatchHandler;
import xyz.dysaido.onevsonegame.arena.ArenaManager;
import xyz.dysaido.onevsonegame.setting.Config;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.Logger;
import xyz.dysaido.onevsonegame.util.Reflection;
import xyz.dysaido.onevsonegame.util.ServerVersion;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public final class OneVSOneGame extends JavaPlugin {
    private static final String TAG = "MAIN";
    private static OneVSOneGame instance;
    private final String pluginVersion = getDescription().getVersion();
    private FileManager ringConfig;
    private ArenaManager arenaManager;
    private MatchHandler matchHandler;
    private Config config;
    private ServerVersion serverVersion;
    private boolean newUpdate = false;

    public static OneVSOneGame getInstance() {
        synchronized (OneVSOneGame.class) {
            return instance;
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        getVersion(s -> {
            if (!pluginVersion.equals(s)) newUpdate = true;
        });
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

    public boolean hasNewVersion() {
        return newUpdate;
    }

    public void getVersion(final Consumer<String> consumer) {
        int resourceId = 97786;
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
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
        Logger.information(TAG, "1v1Event plugin was disabled");
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
