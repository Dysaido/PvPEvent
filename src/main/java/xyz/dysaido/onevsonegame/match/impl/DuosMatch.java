package xyz.dysaido.onevsonegame.match.impl;

import org.bukkit.Bukkit;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.event.SoloGamePlayerWinEvent;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.match.MatchState;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.MatchHelper;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.Optional;

public class DuosMatch extends BaseMatch {

    private Pair<MatchPlayer, MatchPlayer> duo1;
    private Pair<MatchPlayer, MatchPlayer> duo2;

    public DuosMatch(OneVSOneGame plugin, Ring ring) {
        super("DuosMatch", plugin, ring);
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
                    queue.getPlayersByState(PlayerState.FIGHT).forEach(player -> player.reset(ring.getLobby(), PlayerState.QUEUE));
                    nextRound();
                } else if (queue.getPlayersByState(PlayerState.QUEUE).size() == 0 && !hasFighting()) {
                    queue.getPlayersByState(PlayerState.FIGHT).forEach(player -> {
                        Format.broadcast(Settings.EVENT_WINNER_MESSAGE.replace("{player}", player.getPlayer().getName()));
                        player.reset(ring.getWorldSpawn(), PlayerState.WINNER);
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
                    queue.getPlayersByState(PlayerState.SPECTATOR).stream().map(MatchPlayer::getPlayer).forEach(player -> {
                        player.teleport(ring.getWorldSpawn());
                    });
                    state = MatchState.END;
                }
                break;
            default:
                plugin.getMatchManager().destroy();
        }
    }

    @Override
    public void nextRound() {
        this.round++;
        this.round++;
        String text = Settings.NEXT_ROUND;
        Pair<Pair<MatchPlayer, MatchPlayer>, Pair<MatchPlayer, MatchPlayer>> opponents = this.queue.randomizedDuosOpponents();
        // Bukkit.getServer().getPluginManager().callEvent(new SoloGameNextRoundEvent(this, opponents));
        duo1 = opponents.getKey();
        duo2 = opponents.getValue();
        queue.addFight(duo1.getKey()).setup(ring.getSpawn1());
        queue.addFight(duo1.getValue()).setup(ring.getSpawn1());
        queue.addFight(duo2.getKey()).setup(ring.getSpawn2());
        queue.addFight(duo2.getValue()).setup(ring.getSpawn2());
        text = text.replace("{duo1}", duo1.getKey().getName() + " " + duo1.getValue().getName());
        text = text.replace("{duo2}", duo2.getKey().getName() + " " + duo2.getValue().getName());
        text = text.replace("{round}", String.valueOf(round));
        Format.broadcast(text);
    }

    @Override
    public boolean hasFighting() {
        return MatchHelper.hasFighting(duo1, duo2);
    }

    @Override
    public boolean shouldNextRound() {
        if (duo1 == null || duo2 == null) return super.shouldNextRound();
        return MatchHelper.shouldNextRound(duo1, duo2) && super.shouldNextRound();
    }

    @Override
    public boolean shouldEnd() {
        return !queue.getPlayersByState(PlayerState.WINNER).isEmpty() || queue.getMatchPlayers().size() <= 3;
    }
}
