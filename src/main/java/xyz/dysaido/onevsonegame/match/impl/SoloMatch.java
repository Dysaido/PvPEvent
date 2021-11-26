package xyz.dysaido.onevsonegame.match.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.event.SoloGameNextRoundEvent;
import xyz.dysaido.onevsonegame.event.GamePlayerJoinEvent;
import xyz.dysaido.onevsonegame.event.GamePlayerLeaveEvent;
import xyz.dysaido.onevsonegame.event.SoloGamePlayerWinEvent;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.match.MatchState;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.Objects;
import java.util.Optional;

public class SoloMatch extends BaseMatch {

    public SoloMatch(OneVSOneGame plugin, Ring ring) {
        super("SoloMatch", plugin, ring);
    }

    @Override
    public void join(Player player) {
        Objects.requireNonNull(player);
        if (waiting > 0) {
            if (!queue.contains(player)) {
                MatchPlayer matchPlayer = new MatchPlayer(this, player);
                Bukkit.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent(this, matchPlayer));
                queue.addMatchPlayer(matchPlayer);
                Format.broadcast(Settings.JOIN_MESSAGE.replace("{player}", player.getName()));
            } else {
                player.sendMessage(Format.colored(Settings.ALREADY_JOINED_MESSAGE));
            }
        } else {
            player.sendMessage(Format.colored(Settings.EVENT_NOT_AVAILABLE_MESSAGE));
        }
    }

    @Override
    public void leave(Player player) {
        Objects.requireNonNull(player);
        if (queue.contains(player)) {
            MatchPlayer matchPlayer = queue.findByPlayer(player);
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent(this, matchPlayer));
            matchPlayer.reset(ring.getWorldSpawn(), PlayerState.SPECTATOR);
            queue.removeMatchPlayer(matchPlayer);
            Format.broadcast(Settings.LEAVE_MESSAGE.replace("{player}", player.getName()));
        }
    }

    @Override
    public void loop() {
        switch (state) {
            case WAITING:
                Format.broadcastClickable(Settings.WAITING_MESSAGE.replace("{second}", String.valueOf(waiting)));
                waiting--;
                if (waiting == 0) {
                    if (queue.shouldEnd()) {
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
                    if (queue.shouldEnd()) {
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
        Format.broadcast(Settings.NEXT_ROUND.replace("{round}", String.valueOf(round)));
        Pair<MatchPlayer, MatchPlayer> opponents = this.queue.randomizedOpponents();
        Bukkit.getServer().getPluginManager().callEvent(new SoloGameNextRoundEvent(this, opponents));
        MatchPlayer damager = opponents.getKey();
        MatchPlayer victim = opponents.getValue();
        queue.addFight(damager).setup(ring.getSpawn1());
        queue.addFight(victim).setup(ring.getSpawn2());
    }

    @Override
    public boolean hasFighting() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() == 2;
    }

    @Override
    public boolean shouldNextRound() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() <= 1 && super.shouldNextRound();
    }
}
