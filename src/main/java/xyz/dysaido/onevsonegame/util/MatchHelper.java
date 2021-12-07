package xyz.dysaido.onevsonegame.util;

import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;

import java.util.Objects;

public class MatchHelper {

    public static boolean shouldNextRound(Pair<MatchPlayer, MatchPlayer> pair1, Pair<MatchPlayer, MatchPlayer> pair2) {
        return shouldNextRound(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean shouldNextRound(MatchPlayer player1, MatchPlayer player2, MatchPlayer player3, MatchPlayer player4) {
        return shouldNextRound(player1, player3) || shouldNextRound(player1, player4) ||
                shouldNextRound(player2, player3) || shouldNextRound(player2, player4);
    }

    public static boolean shouldNextRound(MatchPlayer player, MatchPlayer target) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(target);
        return (hasFighting(player) && target.getState() != PlayerState.FIGHT && target.getState().equals(PlayerState.WINNER)) ||
                (hasFighting(target) && player.getState() != PlayerState.FIGHT && player.getState().equals(PlayerState.WINNER));
    }

    public static boolean hasFighting(Pair<MatchPlayer, MatchPlayer> pair1, Pair<MatchPlayer, MatchPlayer> pair2) {
        return hasFighting(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean hasFighting(MatchPlayer damager1, MatchPlayer damager2, MatchPlayer victim1, MatchPlayer victim2) {
        return hasFighting(damager1, victim1) || hasFighting(damager1, victim2) ||
                hasFighting(damager2, victim1) || hasFighting(damager2, victim2);
    }

    public static boolean hasFighting(MatchPlayer player, MatchPlayer target) {
        return hasFighting(player) && hasFighting(target);
    }

    public static boolean hasFighting(MatchPlayer player) {
        Objects.requireNonNull(player);
        return player.getState().equals(PlayerState.FIGHT);
    }

    public static boolean notEqual(Pair<MatchPlayer, MatchPlayer> pair1, Pair<MatchPlayer, MatchPlayer> pair2) {
        return notEqual(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean notEqual(MatchPlayer player1, MatchPlayer player2, MatchPlayer player3, MatchPlayer player4) {
        return notEqual(player1, player2) && notEqual(player1, player3) && notEqual(player1, player4)
                && notEqual(player2, player3) && notEqual(player2, player4) && notEqual(player3, player4);
    }

    public static boolean notEqual(MatchPlayer object1, MatchPlayer object2) {
        Objects.requireNonNull(object1);
        Objects.requireNonNull(object2);
        return !object1.equals(object2);
    }
}
