package xyz.dysaido.onevsonegame.match;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.impl.SoloMatch;
import xyz.dysaido.onevsonegame.ring.Ring;

import java.util.Optional;

public class MatchManager {

    private final OneVSOneGame plugin;
    private volatile BaseMatch match;

    public MatchManager(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    public BaseMatch createSolo(Ring ring) {
        if (!isNull()) this.match.stop();
        this.match = new SoloMatch(plugin, ring);
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
