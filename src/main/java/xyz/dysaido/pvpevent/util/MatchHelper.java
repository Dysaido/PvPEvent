package xyz.dysaido.pvpevent.util;

import xyz.dysaido.pvpevent.match.model.Participant;

import java.util.Objects;

import static xyz.dysaido.pvpevent.match.model.Participant.*;

public class MatchHelper {

    public static boolean shouldNextRound(Pair<Participant, Participant> pair1, Pair<Participant, Participant> pair2) {
        return shouldNextRound(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean shouldNextRound(Participant player1, Participant player2, Participant player3, Participant player4) {
        return shouldNextRound(player1, player3) || shouldNextRound(player1, player4) ||
                shouldNextRound(player2, player3) || shouldNextRound(player2, player4);
    }

    public static boolean shouldNextRound(Participant player, Participant target) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(target);
        return (hasFighting(player) && target.getState() != State.FIGHT && target.getState().equals(State.WINNER)) ||
                (hasFighting(target) && player.getState() != State.FIGHT && player.getState().equals(State.WINNER));
    }

    public static boolean hasFighting(Pair<Participant, Participant> pair1, Pair<Participant, Participant> pair2) {
        return hasFighting(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean hasFighting(Participant damager1, Participant damager2, Participant victim1, Participant victim2) {
        return hasFighting(damager1, victim1) || hasFighting(damager1, victim2) ||
                hasFighting(damager2, victim1) || hasFighting(damager2, victim2);
    }

    public static boolean hasFighting(Participant player, Participant target) {
        return hasFighting(player) && hasFighting(target);
    }

    public static boolean hasFighting(Participant player) {
        Objects.requireNonNull(player);
        return player.getState().equals(State.FIGHT);
    }

    public static boolean notEqual(Pair<Participant, Participant> pair1, Pair<Participant, Participant> pair2) {
        return notEqual(pair1.getKey(), pair1.getValue(), pair2.getKey(), pair2.getValue());
    }

    public static boolean notEqual(Participant player1, Participant player2, Participant player3, Participant player4) {
        return notEqual(player1, player2) && notEqual(player1, player3) && notEqual(player1, player4)
                && notEqual(player2, player3) && notEqual(player2, player4) && notEqual(player3, player4);
    }

    public static boolean notEqual(Participant object1, Participant object2) {
        Objects.requireNonNull(object1);
        Objects.requireNonNull(object2);
        return !object1.equals(object2);
    }
}
