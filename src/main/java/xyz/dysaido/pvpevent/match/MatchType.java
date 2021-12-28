package xyz.dysaido.pvpevent.match;

import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.match.impl.DuosMatch;
import xyz.dysaido.pvpevent.match.impl.QuadsMatch;
import xyz.dysaido.pvpevent.match.impl.SolosMatch;
import xyz.dysaido.pvpevent.arena.Arena;

import java.util.function.BiFunction;

public enum MatchType {
    SOLOS(SolosMatch::new),
    DUOS(DuosMatch::new),
    QUADS(QuadsMatch::new);

    private final BiFunction<PvPEvent, Arena, BaseMatch> factory;

    MatchType(BiFunction<PvPEvent, Arena, BaseMatch> factory) {
        this.factory = factory;
    }

    public BaseMatch createMatch(PvPEvent plugin, Arena arena) {
        return factory.apply(plugin, arena);
    }

}
