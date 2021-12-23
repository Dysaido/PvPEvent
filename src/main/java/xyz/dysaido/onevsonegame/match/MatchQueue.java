package xyz.dysaido.onevsonegame.match;

import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;

import java.util.*;
import java.util.stream.Collectors;

public class MatchQueue<P extends MatchPlayer> {
    private final Queue<P> pendingQueue;
    private final Set<P> fighters;

    public MatchQueue(BaseMatch match) {
        this.pendingQueue = new LinkedList<>();
        int size = 0;
        switch (match.type) {
            case SOLOS:
                size = 2;
                break;
            case DUOS:
                size = 4;
                break;
            case QUADS:
                size = 8;
                break;
        }
        this.fighters = new HashSet<>(size);
    }

    public void addQueue(P player) {
        Objects.requireNonNull(player);
        synchronized (this.pendingQueue) {
            player.setState(PlayerState.QUEUE);
            if (containsQueue(player.getPlayer())) return;
            this.pendingQueue.add(player);
        }
    }

    public void offerQueue(P player) {
        Objects.requireNonNull(player);
        synchronized (this.pendingQueue) {
            player.setState(PlayerState.QUEUE);
            if (containsQueue(player.getPlayer())) return;
            this.pendingQueue.offer(player);
        }
    }

    public void removeQueue(P player) {
        Objects.requireNonNull(player);
        synchronized (this.pendingQueue) {
            player.setState(null);
            this.pendingQueue.remove(player);
        }
    }

    public void addFighter(P player) {
        Objects.requireNonNull(player);
        synchronized (this.fighters) {
            player.setState(PlayerState.FIGHT);
            this.fighters.add(player);
        }
    }

    public void removeFighter(P player) {
        Objects.requireNonNull(player);
        synchronized (this.fighters) {
            player.setState(null);
            this.fighters.remove(player);
        }
    }

    public P pull() {
        synchronized (this.pendingQueue) {
            if (!this.pendingQueue.isEmpty()) {
                return this.pendingQueue.remove();
            } else {
                return null;
            }
        }
    }

    public boolean containsQueue(Player player) {
        Objects.requireNonNull(player);
        synchronized (this.pendingQueue) {
            return pendingQueue.stream().map(MatchPlayer::getPlayer).anyMatch(internal -> internal.equals(player));
        }
    }

    public boolean containsFighter(Player player) {
        Objects.requireNonNull(player);
        synchronized (this.fighters) {
            return fighters.stream().map(MatchPlayer::getPlayer).anyMatch(internal -> internal.equals(player));
        }
    }

    public P findQueueByName(String name) {
        Objects.requireNonNull(name);
        synchronized (this.pendingQueue) {
            return this.pendingQueue.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
        }
    }

    public P findFighterByName(String name) {
        Objects.requireNonNull(name);
        synchronized (this.fighters) {
            return this.fighters.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
        }
    }

    public List<MatchPlayer> getFightersByState(PlayerState state) {
        Objects.requireNonNull(state);
        synchronized (this.fighters) {
            return this.fighters.stream().filter(player -> player.getState().equals(state)).collect(Collectors.toList());
        }
    }

    public Queue<P> getPendingQueue() {
        return pendingQueue;
    }

    public Set<P> getFighters() {
        return fighters;
    }
}
