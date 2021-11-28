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
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.Optional;

public class DuosMatch extends BaseMatch {

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
                if (shouldNextRound()) {
                    Optional<MatchPlayer> matchPlayer = queue.getPlayersByState(PlayerState.FIGHT).stream().findFirst();
                    matchPlayer.ifPresent(internal -> internal.reset(ring.getLobby(), PlayerState.QUEUE));
                    nextRound();
                } else if (queue.getPlayersByState(PlayerState.QUEUE).size() == 0 && !hasFighting()) {
                    Optional<MatchPlayer> matchPlayer = queue.getPlayersByState(PlayerState.FIGHT).stream().findFirst();
                    matchPlayer.ifPresent(internal -> {
                        Bukkit.getServer().getPluginManager().callEvent(new SoloGamePlayerWinEvent(this, internal));
                        Format.broadcast(Settings.EVENT_WINNER_MESSAGE.replace("{player}", internal.getPlayer().getName()));
                        internal.reset(ring.getWorldSpawn(), PlayerState.WINNER);
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
        Pair<MatchPlayer, MatchPlayer> duo1 = opponents.getKey();
        Pair<MatchPlayer, MatchPlayer> duo2 = opponents.getValue();
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
        return queue.getPlayersByState(PlayerState.FIGHT).size() >= 2;
    }

    @Override
    public boolean shouldNextRound() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() <= 1 && super.shouldNextRound();
    }

    @Override
    public boolean shouldEnd() {
        return !queue.getPlayersByState(PlayerState.WINNER).isEmpty() || queue.getMatchPlayers().size() <= 3;
    }
}
