package xyz.dysaido.onevsonegame.util;

import xyz.dysaido.onevsonegame.match.model.MatchPlayer;

public class MatchHelper {

    public static boolean notEqual(Pair<MatchPlayer, MatchPlayer> pair1, Pair<MatchPlayer, MatchPlayer> pair2) {
        return notEqual(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean notEqual(MatchPlayer player1, MatchPlayer player2, MatchPlayer player3, MatchPlayer player4) {
        return notEqual(player1, player2) && notEqual(player1, player3) && notEqual(player1, player4)
                && notEqual(player2, player3) && notEqual(player2, player4) && notEqual(player3, player4);
    }

    public static boolean notEqual(MatchPlayer object1, MatchPlayer object2) {
        return !object1.equals(object2);
    }
}
