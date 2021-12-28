package xyz.dysaido.pvpevent.match;

import org.bukkit.entity.Player;
import xyz.dysaido.pvpevent.match.model.Participant;
import xyz.dysaido.pvpevent.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.dysaido.pvpevent.match.model.Participant.State;

public class MatchQueue<P extends Participant> {
    private final List<P> participants = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    public MatchQueue(BaseMatch match) {
//        this.pendingQueue = new LinkedList<>();
//        int size = 0;
//        switch (match.type) {
//            case SOLOS:
//                size = 2;
//                break;
//            case DUOS:
//                size = 4;
//                break;
//            case QUADS:
//                size = 8;
//                break;
//        }
//        this.fighters = new HashSet<>(size);
    }

    public void addParticipant(P participant) {
        Objects.requireNonNull(participant);
        participant.setState(State.QUEUE);
        if (contains(participant.getPlayer())) return;
        this.participants.add(participant);
    }

    public void removeParticipant(P participant) {
        Objects.requireNonNull(participant);
        participant.setState(null);
        this.participants.remove(participant);
    }

    public Pair<P, P> randomizedSolosOpponents() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Map<Integer, P> queues = getParticipantsByState(State.QUEUE)
                .collect(Collectors.toMap(x -> atomicInteger.getAndIncrement(), Function.identity()));
        P p1, p2;
        do {
            p1 = queues.get(random.nextInt(queues.size()));
            p2 = queues.get(random.nextInt(queues.size()));
        } while (p1 == p2);
        return new Pair<>(p1, p2);
    }

    public boolean contains(Player player) {
        Objects.requireNonNull(player);
        return participants.stream().map(Participant::getPlayer).anyMatch(internal -> internal.equals(player));
    }

    public P findParticipantByName(String name) {
        Objects.requireNonNull(name);
        return this.participants.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public Stream<P> getParticipantsByState(State state) {
        Objects.requireNonNull(state);
        return this.participants.stream().filter(player -> player.getState().equals(state));
    }

    public List<P> getParticipants() {
        return participants;
    }

}
