package xyz.dysaido.pvpevent.match.impl;

import org.bukkit.entity.Player;
import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.match.BaseMatch;
import xyz.dysaido.pvpevent.match.MatchType;
import xyz.dysaido.pvpevent.match.model.Participant;
import xyz.dysaido.pvpevent.arena.Arena;
import xyz.dysaido.pvpevent.util.Pair;

public class DuosMatch extends BaseMatch {

    private Pair<Participant, Participant> duo1;
    private Pair<Participant, Participant> duo2;

    public DuosMatch(PvPEvent plugin, Arena arena) {
        super(plugin, arena, MatchType.DUOS);
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
