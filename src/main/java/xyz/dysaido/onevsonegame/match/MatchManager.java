package xyz.dysaido.onevsonegame.match;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.ring.Ring;

import java.util.Optional;

public class MatchManager {

    private final OneVSOneGame plugin;
    private volatile Match match;

    public MatchManager(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    public void make() {

    }

    public void destruction() {

    }

    public Match create(Ring ring) {
        if (!isNull()) this.match.stop();
        this.match = new Match(plugin, ring);
        this.match.start();
        return match;
//            throw new RuntimeException("The match has already been created. Please, you have to destroy previous match that you wanna create a new match.");
    }

    public Optional<Match> getMatch() {
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
