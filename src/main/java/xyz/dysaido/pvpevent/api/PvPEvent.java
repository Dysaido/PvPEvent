package xyz.dysaido.pvpevent.api;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.api.model.Match;
import xyz.dysaido.pvpevent.command.ParentCommand;
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;
import xyz.dysaido.pvpevent.model.manager.AutosetManager;
import xyz.dysaido.pvpevent.model.manager.KitManager;
import xyz.dysaido.pvpevent.model.manager.UserManager;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PvPEvent {

    void enable();

    boolean isActiveMatch();

    void disable();

    void reload();

    void reloadConfig();

    void setMainMatch(Match<UUID> mainMatch);

    Optional<Match<UUID>> getMainMatch();

    boolean hasMainMatch();

    void reloadStorages();

    JavaPlugin getPlugin();

    File getConfigFile();

    ParentCommand getCommandManager();

    ArenaManager getArenaManager();

    KitManager getKitManager();

    UserManager getUserManager();

    Map<String, User> getTop10Wins();

    AutosetManager getAutosetManager();
}
