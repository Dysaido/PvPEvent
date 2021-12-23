package xyz.dysaido.onevsonegame.match;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.impl.DuosMatch;
import xyz.dysaido.onevsonegame.match.impl.QuadsMatch;
import xyz.dysaido.onevsonegame.match.impl.SolosMatch;
import xyz.dysaido.onevsonegame.arena.Arena;

import java.util.function.BiFunction;

public enum MatchType {
    SOLOS(SolosMatch::new),
    DUOS(DuosMatch::new),
    QUADS(QuadsMatch::new);

    private final BiFunction<OneVSOneGame, Arena, BaseMatch> factory;

    MatchType(BiFunction<OneVSOneGame, Arena, BaseMatch> factory) {
        this.factory = factory;
    }

    public BaseMatch createMatch(OneVSOneGame plugin, Arena arena) {
        return factory.apply(plugin, arena);
    }

}
