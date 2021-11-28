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
    private final BaseMatch match;

    public MatchQueue(BaseMatch match) {
        this.match = match;
    }

    public MatchPlayer addMatchPlayer(MatchPlayer player) {
        Objects.requireNonNull(player);
        if (!players.contains(player)) players.add(player);
        addQueue(player);
        return player;
    }

    public MatchPlayer removeMatchPlayer(MatchPlayer player) {
        Objects.requireNonNull(player);
        players.remove(player);
        return player;
    }

    public MatchPlayer addQueue(MatchPlayer player) {
        Objects.requireNonNull(player);
        player.setState(PlayerState.QUEUE);
        return player;
    }

    public MatchPlayer addFight(MatchPlayer player) {
        Objects.requireNonNull(player);
        player.setState(PlayerState.FIGHT);
        return player;
    }

    public MatchPlayer addSpectator(MatchPlayer player) {
        Objects.requireNonNull(player);
        player.setState(PlayerState.SPECTATOR);
        return player;
    }

    public MatchPlayer findByPlayer(Player player) {
        Objects.requireNonNull(player);
        return players.stream().filter(internal -> internal.getPlayer().equals(player)).findAny().orElse(null);
    }

    public Pair<MatchPlayer, MatchPlayer> randomizedSolosOpponents() {
        MatchPlayer player1, player2;
        do {
            List<MatchPlayer> queues = getPlayersByState(PlayerState.QUEUE);
            player1 = queues.get(random.nextInt(queues.size()));
            player2 = queues.get(random.nextInt(queues.size()));
        } while (player1 == player2);
        return new Pair<>(player1, player2);
    }

    public Pair<Pair<MatchPlayer, MatchPlayer>, Pair<MatchPlayer, MatchPlayer>> randomizedDuosOpponents() {
        MatchPlayer player1, player2, player3, player4;
        List<MatchPlayer> queues = getPlayersByState(PlayerState.QUEUE);
        do {
            player1 = queues.get(random.nextInt(queues.size()));
            player2 = queues.get(random.nextInt(queues.size()));
        } while (player1 == player2);
        Pair<MatchPlayer, MatchPlayer> pair1 = new Pair<>(player1, player2);
        do {
            player3 = queues.get(random.nextInt(queues.size()));
            player4 = queues.get(random.nextInt(queues.size()));
        } while (player3 == player4 && pair1.getKey() == player3 && pair1.getValue() == player3 && pair1.getKey() == player4 && pair1.getValue() == player4);
        Pair<MatchPlayer, MatchPlayer> pair2 = new Pair<>(player3, player4);
        return new Pair<>(pair1, pair2);
    }

    public List<MatchPlayer> getPlayersByState(PlayerState state) {
        Objects.requireNonNull(state);
        return players.stream().filter(internal -> internal.getState().equals(state)).collect(Collectors.toList());
    }

    public boolean contains(Player player) {
        Objects.requireNonNull(player);
        return players.stream().map(MatchPlayer::getPlayer).anyMatch(internal -> internal.equals(player));
    }

    public boolean contains(MatchPlayer player) {
        Objects.requireNonNull(player);
        return players.contains(player);
    }

    public Collection<MatchPlayer> getMatchPlayers() {
        return Collections.unmodifiableList(players);
    }

}
