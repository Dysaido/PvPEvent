package xyz.dysaido.onevsonegame.match;

import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class MatchQueue {

    private final Random random = new Random();
    private final List<MatchPlayer> players = new ArrayList<>();
    private final Match match;
    private Pair<MatchPlayer, MatchPlayer> opponent;

    public MatchQueue(Match match) {
        this.match = match;
    }

    public void addMatchPlayer(MatchPlayer player) {
        Objects.requireNonNull(player);
        if (!players.contains(player)) players.add(player);
    }

    public void removeMatchPlayer(MatchPlayer player) {
        Objects.requireNonNull(player);
        players.remove(player);
    }

    public void addQueue(MatchPlayer player) {
        Objects.requireNonNull(player);
        player.setState(PlayerState.QUEUE);
    }

    public void addFight(MatchPlayer player) {
        Objects.requireNonNull(player);
        player.setState(PlayerState.FIGHT);
    }

    public void addSpectator(MatchPlayer player) {
        Objects.requireNonNull(player);
        player.setState(PlayerState.SPECTATOR);
    }

    public MatchPlayer findByPlayer(Player player) {
        Objects.requireNonNull(player);
        return players.stream().filter(internal -> internal.getPlayer().equals(player)).findAny().orElse(null);
    }

    public MatchPlayer findByPlayerState(Player player, PlayerState state) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(state);
        return players.stream().filter(internal -> internal.getPlayer().equals(player) && internal.getState().equals(state)).findAny().orElse(null);
    }


    public Pair<MatchPlayer, MatchPlayer> randomizedOpponents() {
        if (match.getState().equals(MatchState.FIGHTING)) {
            MatchPlayer player1, player2;
            do {
                player1 = players.get(random.nextInt(players.size()));
                player2 = players.get(random.nextInt(players.size()));
            } while (!player1.equals(player2));
            return opponent = new Pair<>(player1, player2);
        }
        return null;
    }

    public boolean shouldDoEnd() {
        return getPlayersByState(PlayerState.WINNER).size() == 1 || players.size() <= 1;
    }

    public Collection<MatchPlayer> getPlayersByState(PlayerState state) {
        Objects.requireNonNull(state);
        return players.stream().filter(internal -> internal.getState().equals(state)).collect(Collectors.toList());
    }

    public boolean contains(MatchPlayer player) {
        return players.contains(player);
    }

    public Collection<MatchPlayer> getMatchPlayers() {
        return Collections.unmodifiableList(players);
    }

}
