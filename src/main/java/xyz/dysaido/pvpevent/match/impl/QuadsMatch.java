package xyz.dysaido.pvpevent.match.impl;

import org.bukkit.entity.Player;
import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.match.BaseMatch;
import xyz.dysaido.pvpevent.arena.Arena;
import xyz.dysaido.pvpevent.match.MatchType;

public class QuadsMatch extends BaseMatch {

    public QuadsMatch(PvPEvent plugin, Arena arena) {
        super(plugin, arena, MatchType.QUADS);
    }

    @Override
    public void join(Player player) {

    }

    @Override
    public void leave(Player player) {

    }

    @Override
    public void loop() {

    }

    @Override
    public void nextRound() {

    }

    @Override
    public boolean hasFighting() {
        return false;
    }

    @Override
    public boolean shouldEnd() {
        return false;
    }
}
