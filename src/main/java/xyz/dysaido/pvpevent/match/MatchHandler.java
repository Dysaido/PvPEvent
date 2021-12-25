package xyz.dysaido.onevsonegame.match;

import org.bukkit.Bukkit;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.api.MatchStartEvent;
import xyz.dysaido.onevsonegame.api.MatchStopEvent;
import xyz.dysaido.onevsonegame.arena.Arena;

import java.util.Optional;

public class MatchHandler {

    private final OneVSOneGame plugin;
    private BaseMatch match;

    public MatchHandler(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    public boolean host(MatchType type, Arena ring) {
        if (isNull()) {
            this.match = type.createMatch(plugin, ring);
            Bukkit.getServer().getPluginManager().callEvent(new MatchStartEvent(match));
            this.match.start();
            return true;
        }
        return false;
    }

    private boolean isNull() {
        return this.match == null;
    }

    public Optional<BaseMatch> getMatch() {
        return Optional.ofNullable(this.match);
    }

    public void destroy() {
        Bukkit.getServer().getPluginManager().callEvent(new MatchStopEvent(this.match));
        this.match.stop();
        this.match = null;
    }

}
