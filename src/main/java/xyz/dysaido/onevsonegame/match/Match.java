package xyz.dysaido.onevsonegame.match;

import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.ring.Ring;
import xyz.dysaido.onevsonegame.setting.Config;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Pair;

import java.util.Objects;
import java.util.Optional;

public class Match extends MatchTask {

    private final Ring ring;
    private final MatchQueue queue;
    private final OneVSOneGame plugin;
    private volatile MatchState state = MatchState.WAITING;
    private int round = 0;

    private long lastTransaction = 0;

    private int waiting = Config.WAITING;
    private int starting = Config.STARTING;
    private int ending = Config.ENDING;

    public Match(OneVSOneGame plugin, Ring ring) {
        super("Match", plugin);
        this.plugin = plugin;
        this.ring = ring;
        this.queue = new MatchQueue(this);
    }

    @Override
    public void run() {
        long tick = System.currentTimeMillis();
        if (tick - this.lastTransaction >= 1000) {
            this.lastTransaction = tick;
            switch (state) {
                case WAITING:
                    Format.broadcastClickable(Config.WAITING_MESSAGE.replace("{second}", String.valueOf(waiting)));
                    waiting--;
                    if (waiting == 0) {
                        if (queue.shouldEnd()) {
                            state = MatchState.ENDING;
                        } else {
                            Format.broadcast(Config.EVENT_JOIN_FINISHED_MESSAGE);
                            state = MatchState.STARTING;
                        }
                    }
                    break;
                case STARTING:
                    Format.broadcast(Config.EVENT_WILL_START_MESSAGE.replace("{second}", String.valueOf(starting)));
                    starting--;
                    if (starting == 0) {
                        Format.broadcast(Config.EVENT_START_MESSAGE);
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
                            Format.broadcast(Config.EVENT_WINNER_MESSAGE.replace("{player}", internal.getPlayer().getName()));
                            internal.reset(ring.getWorldSpawn(), PlayerState.WINNER);
                        });
                        if (queue.shouldEnd()) {
                            state = MatchState.ENDING;
                        }
                    }
                    break;
                case ENDING:
                    Format.broadcast(Config.EVENT_ENDING_MESSAGE.replace("{second}", String.valueOf(ending)));
                    ending--;
                    if (ending == 0) {
                        Format.broadcast(Config.EVENT_ENDED_MESSAGE);
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
    }

    public void join(Player player) {
        Objects.requireNonNull(player);
        if (waiting > 0) {
            if (!queue.contains(player)) {
                MatchPlayer matchPlayer = new MatchPlayer(this, player);
                queue.addMatchPlayer(matchPlayer);
                Format.broadcast(Config.JOIN_MESSAGE.replace("{player}", player.getName()));
            } else {
                player.sendMessage(Format.colored(Config.ALREADY_JOINED_MESSAGE));
            }
        } else {
            player.sendMessage(Format.colored(Config.EVENT_NOT_AVAILABLE_MESSAGE));
        }
    }

    public void leave(Player player) {
        Objects.requireNonNull(player);
        MatchPlayer matchPlayer = queue.findByPlayer(player);
        if (matchPlayer != null) {
            matchPlayer.reset(ring.getWorldSpawn(), PlayerState.SPECTATOR);
            queue.removeMatchPlayer(matchPlayer);
            Format.broadcast(Config.LEAVE_MESSAGE.replace("{player}", player.getName()));
        }
    }

    private void nextRound() {
        this.round++;
        Format.broadcast(Config.NEXT_ROUND.replace("{round}", String.valueOf(round)));
        Pair<MatchPlayer, MatchPlayer> opponents = this.queue.randomizedOpponents();
        MatchPlayer damager = opponents.getKey();
        MatchPlayer victim = opponents.getValue();
        damager.setup(ring.getSpawn1());
        victim.setup(ring.getSpawn2());
    }

    private boolean hasFighting() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() == 2;
    }

    private boolean shouldNextRound() {
        return queue.getPlayersByState(PlayerState.FIGHT).size() <= 1 && queue.getPlayersByState(PlayerState.QUEUE).size() > 0 && getState().equals(MatchState.FIGHTING) && !queue.shouldEnd();
    }

    public MatchQueue getQueue() {
        return queue;
    }

    public Ring getRing() {
        return ring;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }
}
