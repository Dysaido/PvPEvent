package xyz.dysaido.pvpevent.match.impl;

import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.arena.Arena;
import xyz.dysaido.pvpevent.match.BaseMatch;
import xyz.dysaido.pvpevent.match.MatchState;
import xyz.dysaido.pvpevent.match.MatchType;
import xyz.dysaido.pvpevent.match.model.Participant;
import xyz.dysaido.pvpevent.setting.Settings;
import xyz.dysaido.pvpevent.util.Format;
import xyz.dysaido.pvpevent.util.Pair;

import java.util.Optional;
import java.util.stream.Collectors;

import static xyz.dysaido.pvpevent.match.model.Participant.State;

public class SolosMatch extends BaseMatch {

    public SolosMatch(PvPEvent plugin, Arena arena) {
        super(plugin, arena, MatchType.SOLOS);
    }

    @Override
    public void loop() {
        switch (state) {
            case WAITING:
                if (waiting % Math.max(Settings.WAITING_MODULO , 1) == 0) {
                    Format.broadcastClickable(Settings.WAITING_MESSAGE.replace("{second}", String.valueOf(waiting)));
                }
                waiting--;
                if (waiting == 0) {
                    if (shouldEnd()) {
                        state = MatchState.ENDING;
                    } else {
                        Format.broadcast(Settings.EVENT_JOIN_FINISHED_MESSAGE);
                        state = MatchState.STARTING;
                    }
                }
                break;
            case STARTING:
                Format.broadcast(Settings.EVENT_WILL_START_MESSAGE.replace("{second}", String.valueOf(starting)));
                starting--;
                if (starting == 0) {
                    Format.broadcast(Settings.EVENT_START_MESSAGE);
                    state = MatchState.FIGHTING;
                }
                break;
            case FIGHTING:
                if (shouldNextRound()) {
                    Optional<Participant> matchPlayer = queue.getParticipantsByState(State.FIGHT).findFirst();
                    matchPlayer.ifPresent(internal -> internal.reset(arena.getLobby(), false));
                    nextRound();
                } else if (super.shouldEnd() && !hasFighting()) {
                    Optional<Participant> matchPlayer = queue.getParticipantsByState(State.FIGHT).findFirst();
                    matchPlayer.ifPresent(internal -> {
                        Format.broadcast(Settings.EVENT_WINNER_MESSAGE.replace("{player}", internal.getPlayer().getName()));
                        internal.reset(arena.getWorldSpawn(), false);
                        internal.setState(State.WINNER);
                    });
                    state = MatchState.ENDING;
                }
                break;
            case ENDING:
                Format.broadcast(Settings.EVENT_ENDING_MESSAGE.replace("{second}", String.valueOf(ending)));
                ending--;
                if (ending == 0) {
                    Format.broadcast(Settings.EVENT_ENDED_MESSAGE);
                    queue.getParticipantsByState(State.SPECTATOR).forEach(player -> player.reset(arena.getWorldSpawn(), false));
                    state = MatchState.END;
                }
                break;
            default:
                plugin.getMatchHandler().destroy();
        }
    }

    @Override
    public void nextRound() {
        this.round++;
        String text = Settings.NEXT_ROUND;
        Pair<Participant, Participant> opponents = queue.randomizedSolosOpponents();

        text = text.replace("{player1}", opponents.getKey().getName());
        text = text.replace("{player2}", opponents.getValue().getName());
        text = text.replace("{round}", String.valueOf(round));

        opponents.getKey().setup(arena.getSpawn1());
        opponents.getValue().setup(arena.getSpawn2());

        Format.broadcast(text);
    }

    @Override
    public boolean hasFighting() {
        return queue.getParticipantsByState(State.FIGHT).count() == 2;
    }

    @Override
    public boolean shouldNextRound() {
        return queue.getParticipantsByState(State.FIGHT).count() <= 1 && super.shouldNextRound();
    }

    @Override
    public boolean shouldEnd() {
        return queue.getParticipants().size() <= 1 || queue.getParticipantsByState(State.WINNER).count() == 1 || super.shouldEnd();
    }
}
