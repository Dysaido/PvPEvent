package xyz.dysaido.pvpevent.match;

import org.bukkit.Bukkit;
import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.api.event.MatchStartEvent;
import xyz.dysaido.pvpevent.api.event.MatchStopEvent;
import xyz.dysaido.pvpevent.arena.Arena;

import java.util.Optional;

public class MatchHandler {

    private final PvPEvent plugin;
    private BaseMatch match;

    public MatchHandler(PvPEvent plugin) {
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
