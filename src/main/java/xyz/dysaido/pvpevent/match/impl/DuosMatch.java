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
import xyz.dysaido.onevsonegame.util.MatchHelper;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.List;

public class DuosMatch extends BaseMatch {

    private Pair<MatchPlayer, MatchPlayer> duo1;
    private Pair<MatchPlayer, MatchPlayer> duo2;

    public DuosMatch(OneVSOneGame plugin, Arena arena) {
        super(plugin, arena, MatchType.DUOS);
    }

    @Override
    public void loop() {
        switch (state) {
            case WAITING:
                Format.broadcastClickable(Settings.WAITING_MESSAGE.replace("{second}", String.valueOf(waiting)));
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
                //TODO: modifying
                if (shouldNextRound()) {
                    queue.getFightersByState(PlayerState.FIGHT).forEach(player -> player.reset(arena.getLobby(), false));
                    nextRound();
                } else if (queue.getPendingQueue().size() == 0 && !hasFighting()) {
                    queue.getFighters().forEach(player -> {
                        Format.broadcast(Settings.EVENT_WINNER_MESSAGE.replace("{player}", player.getPlayer().getName()));
                        player.reset(arena.getWorldSpawn(), false);
                        player.setState(PlayerState.WINNER);
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

        this.duo1 = new Pair<>(queue.pull(), queue.pull());
        this.duo2 = new Pair<>(queue.pull(), queue.pull());

        MatchPlayer duo1Player1 = this.duo1.getKey();
        MatchPlayer duo1Player2 = this.duo1.getValue();
        MatchPlayer duo2Player1 = this.duo2.getKey();
        MatchPlayer duo2Player2 = this.duo2.getValue();

        duo1Player1.setup(arena.getSpawn1());
        duo1Player2.setup(arena.getSpawn1());
        duo2Player1.setup(arena.getSpawn2());
        duo2Player2.setup(arena.getSpawn2());

        queue.addFighter(duo1Player1);
        queue.addFighter(duo1Player2);
        queue.addFighter(duo2Player1);
        queue.addFighter(duo2Player2);

        text = text.replace("{duo1}", duo1Player1.getName() + " " + duo1Player2.getName());
        text = text.replace("{duo2}", duo2Player1.getName() + " " + duo2Player2.getName());
        text = text.replace("{round}", String.valueOf(round));

        Format.broadcast(text);
    }

    @Override
    public boolean hasFighting() {
        List<MatchPlayer> fighters = queue.getFightersByState(PlayerState.FIGHT);
        fighters.forEach(player -> {

        });
        return MatchHelper.hasFighting(duo1, duo2);
    }

    @Override
    public boolean shouldNextRound() {
        if (duo1 == null || duo2 == null) return super.shouldNextRound();
        return MatchHelper.shouldNextRound(duo1, duo2) && super.shouldNextRound();
    }

    @Override
    public boolean shouldEnd() {
        return !queue.getFightersByState(PlayerState.WINNER).isEmpty() || queue.getPendingQueue().size() + queue.getFighters().size() <= 3;
    }
}
