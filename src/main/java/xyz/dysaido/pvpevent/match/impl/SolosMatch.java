package xyz.dysaido.onevsonegame.match.impl;

import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.match.MatchState;
import xyz.dysaido.onevsonegame.match.MatchType;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.arena.Arena;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.Optional;

public class SolosMatch extends BaseMatch {

    public SolosMatch(OneVSOneGame plugin, Arena arena) {
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
                    Optional<MatchPlayer> matchPlayer = queue.getFighters().stream().findFirst();
                    matchPlayer.ifPresent(internal -> internal.reset(arena.getLobby(), false));
                    nextRound();
                } else if (queue.getPendingQueue().size() == 0 && !hasFighting()) {
                    Optional<MatchPlayer> matchPlayer = queue.getFighters().stream().findFirst();
                    matchPlayer.ifPresent(internal -> {
                        Format.broadcast(Settings.EVENT_WINNER_MESSAGE.replace("{player}", internal.getPlayer().getName()));
                        internal.reset(arena.getWorldSpawn(), false);
                        internal.setState(PlayerState.WINNER);
                    });
                    if (shouldEnd()) {
                        state = MatchState.ENDING;
                    }
                }
                break;
            case ENDING:
                Format.broadcast(Settings.EVENT_ENDING_MESSAGE.replace("{second}", String.valueOf(ending)));
                ending--;
                if (ending == 0) {
                    Format.broadcast(Settings.EVENT_ENDED_MESSAGE);
                    queue.getPendingQueue().forEach(player -> {
                        player.reset(arena.getWorldSpawn(), false);
                    });
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

        MatchPlayer damager = queue.pull();
        MatchPlayer victim = queue.pull();

        text = text.replace("{player1}", damager.getName());
        text = text.replace("{player2}", victim.getName());
        text = text.replace("{round}", String.valueOf(round));

        damager.setup(arena.getSpawn1());
        victim.setup(arena.getSpawn2());

        queue.addFighter(damager);
        queue.addFighter(victim);

        Format.broadcast(text);
    }

    @Override
    public boolean hasFighting() {
        return queue.getFightersByState(PlayerState.FIGHT).size() == 2;
    }

    @Override
    public boolean shouldNextRound() {
        return queue.getFighters().size() <= 1 && super.shouldNextRound();
    }

    @Override
    public boolean shouldEnd() {
        return queue.getFightersByState(PlayerState.WINNER).size() == 1 || queue.getPendingQueue().size() + queue.getFighters().size() <= 1;
    }
}
