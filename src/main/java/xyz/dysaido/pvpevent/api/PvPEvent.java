package xyz.dysaido.pvpevent.api;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.command.ParentCommand;
import xyz.dysaido.pvpevent.api.model.Match;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;
import xyz.dysaido.pvpevent.model.manager.KitManager;

import java.io.File;
import java.util.Optional;

public interface PvPEvent {

    void enable();

    boolean isActiveMatch();

    void disable();



    void reload();

    void setMainMatch(Match mainMatch);

    Optional<Match> getMainMatch();

    boolean hasMainMatch();

    void reloadStorages();

    JavaPlugin getPlugin();

    File getConfigFile();

    ParentCommand getCommandManager();

    ArenaManager getArenaManager();

    KitManager getKitManager();
}
