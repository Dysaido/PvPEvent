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

    public Pair<MatchPlayer, MatchPlayer> randomizedOpponents() {
        MatchPlayer player1, player2;
        do {
            List<MatchPlayer> queues = getPlayersByState(PlayerState.QUEUE);
            player1 = queues.get(random.nextInt(queues.size()));
            player2 = queues.get(random.nextInt(queues.size()));
        } while (player1 == player2);
        return opponent = new Pair<>(player1, player2);
    }

    public boolean shouldEnd() {
        return getPlayersByState(PlayerState.WINNER).size() == 1 || players.size() <= 1;
    }

    public List<MatchPlayer> getPlayersByState(PlayerState state) {
        Objects.requireNonNull(state);
        return players.stream().filter(internal -> internal.getState().equals(state)).collect(Collectors.toList());
    }

    public boolean contains(Player player) {
        return players.stream().map(MatchPlayer::getPlayer).anyMatch(internal -> internal.equals(player));
    }

    public boolean contains(MatchPlayer player) {
        return players.contains(player);
    }

    public Collection<MatchPlayer> getMatchPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Pair<MatchPlayer, MatchPlayer> getOpponent() {
        return opponent;
    }
}
