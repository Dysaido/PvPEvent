package xyz.dysaido.onevsonegame.match.impl;

import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.arena.Arena;
import xyz.dysaido.onevsonegame.match.MatchType;

public class QuadsMatch extends BaseMatch {

    public QuadsMatch(OneVSOneGame plugin, Arena arena) {
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
