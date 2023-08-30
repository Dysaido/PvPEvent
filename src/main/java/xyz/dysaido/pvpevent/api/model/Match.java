package xyz.dysaido.pvpevent.api.model;

import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.match.MatchState;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Match<I> {

    void join(I identifier);

    void leave(I identifier);

    void spectate(I identifier);

    void onDeath(I identifier, PlayerDeathEvent event);

    void onDestroy();

    Match<I> onCreate(PvPEvent pvpEvent, List<Integer> announcements);

    void onStartTask();

    void onPause();

    void onStopTask();

    void nextRound();

    boolean isOver();

    boolean isFull();

    boolean isInsufficient();

    void setState(MatchState state);

    MatchState getState();

    boolean hasParticipant(I identifier);

    ParticipantStatus getParticipantStatus(I identifier);

    Map<I, Participant> getParticipantsByUUD();

    Map<I, ParticipantStatus> getStatusByUUID();
}
