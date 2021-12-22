package xyz.dysaido.onevsonegame.match;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.impl.DuosMatch;
import xyz.dysaido.onevsonegame.match.impl.SolosMatch;
import xyz.dysaido.onevsonegame.ring.Ring;

import java.util.Optional;

public class MatchManager {

    private final OneVSOneGame plugin;
    private volatile BaseMatch match;

    public MatchManager(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    public boolean create(MatchType type, Ring ring) {
        if (isNull()) {
            this.match = type.createMatch(plugin, ring);
            this.match.start();
            return true;
        } else {
            return false;
        }
    }

    public BaseMatch createSolos(Ring ring) {
        if (!isNull()) this.match.stop();
        this.match = new SolosMatch(plugin, ring);
        this.match.start();
        return match;
//            throw new RuntimeException("The match has already been created. Please, you have to destroy previous match that you wanna create a new match.");
    }

    public Optional<BaseMatch> getMatch() {
        return Optional.ofNullable(match);
    }

    public void destroy() {
        this.match.stop();
        this.match = null;
    }

    public boolean isNull() {
        return match == null;
    }

}
