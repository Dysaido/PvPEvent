package xyz.dysaido.pvpevent.api.model;

import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.match.MatchState;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;

import java.util.Map;
import java.util.UUID;

public interface Match {

    void join(UUID identifier);

    void leave(UUID identifier);

    void spectate(UUID identifier);

    void onDeath(UUID identifier, PlayerDeathEvent event);

    Match onCreate(PvPEvent pvpEvent, int modulo);

    void onDestroy();

    void onStartTask();

    void onPause();

    void onStopTask();

    void nextRound();

    void setState(MatchState state);

    MatchState getState();

    Map<UUID, Participant> getParticipantsByUUD();

    Map<UUID, ParticipantStatus> getStatusByUUID();
}
