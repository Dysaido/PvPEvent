package xyz.dysaido.onevsonegame.match;

import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.*;

public class MatchQueue {

    private final Random random = new Random();
    private final List<MatchPlayer> players = new ArrayList<>();
    private final List<MatchPlayer> lastPlayers = new ArrayList<>();
    private final Match match;
    private Pair<MatchPlayer, MatchPlayer> opponent;

    public MatchQueue(Match match) {
        this.match = match;
    }

    public void addQueue(MatchPlayer player) {
        Objects.requireNonNull(player);
        if (!players.contains(player)) players.add(player);
        player.setState(PlayerState.QUEUE);
    }

    public MatchPlayer findMatchPlayerByPlayer(Player player) {
        MatchPlayer matchPlayer = findQueuedByPlayer(player);
        return matchPlayer == null ? findSpectatedByPlayer(player) : matchPlayer;
    }

    public MatchPlayer findQueuedByPlayer(Player player) {
        Objects.requireNonNull(player);
        return players.stream().filter(internal -> internal.getPlayer().equals(player)).findAny().orElse(null);
    }

    public MatchPlayer findSpectatedByPlayer(Player player) {
        Objects.requireNonNull(player);
        return lastPlayers.stream().filter(internal -> internal.getPlayer().equals(player)).findAny().orElse(null);
    }

    public void removeQueue(MatchPlayer player) {
        Objects.requireNonNull(player);
        players.remove(player);
        player.setState(PlayerState.SPECTATOR);
        lastPlayers.add(player);
    }

    public void removeSpectate(MatchPlayer player) {
        Objects.requireNonNull(player);
        lastPlayers.remove(player);
    }

    public void fullRemove(MatchPlayer player) {
        Objects.requireNonNull(player);
        players.remove(player);
        lastPlayers.remove(player);
    }

    public Pair<MatchPlayer, MatchPlayer> getRandomizedOpponents() {
        if (match.getState().equals(MatchState.FIGHTING)) {
            MatchPlayer player1, player2;
            do {
                player1 = players.get(random.nextInt(players.size()));
                player2 = players.get(random.nextInt(players.size()));
            } while (player1.equals(player2));
            opponent = new Pair<>(player1, player2);
        }
        return opponent;
    }

    public boolean contains(MatchPlayer player) {
        return players.contains(player) || lastPlayers.contains(player);
    }

    public Collection<MatchPlayer> getQueued() {
        return Collections.unmodifiableList(players);
    }

    public Collection<MatchPlayer> getSpectated() {
        return Collections.unmodifiableList(lastPlayers);
    }
}
