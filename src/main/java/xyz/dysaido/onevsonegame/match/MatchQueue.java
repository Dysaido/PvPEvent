package xyz.dysaido.onevsonegame.match;

import xyz.dysaido.onevsonegame.match.model.MatchPlayer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatchQueue {

    private final Queue<MatchPlayer> players = new ConcurrentLinkedQueue<>();



    public void addQueue(MatchPlayer player) {
        players.add(player);
    }

    public void removeQueue(MatchPlayer player) {
        players.remove(player);
    }


}
