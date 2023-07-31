package xyz.dysaido.pvpevent.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.api.event.MatchDestroyEvent;
import xyz.dysaido.pvpevent.api.event.MatchJoinEvent;
import xyz.dysaido.pvpevent.api.event.MatchLeaveEvent;
import xyz.dysaido.pvpevent.api.event.MatchStartEvent;
import xyz.dysaido.pvpevent.api.model.Match;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.listener.MatchListener;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.util.BukkitHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractMatch implements Match {

    protected final String present;
    protected final Arena arena;
    protected final Kit<Player> playerKit;
    protected final Map<UUID, Participant> participantsByUUD;
    protected final Map<UUID, ParticipantStatus> statusByUUID;
    protected final MatchListener matchListener;
    protected PvPEvent pvpEvent;
    protected MatchState state;
    protected BukkitTask task;

    protected AbstractMatch(PvPEventPlugin pvpEvent, String present, Arena arena) {
        this.present = present;
        this.arena = arena;
        this.state = MatchState.INACTIVE;
        this.participantsByUUD = new ConcurrentHashMap<>(arena.getCapacity());
        this.statusByUUID = new ConcurrentHashMap<>(arena.getCapacity());
        this.matchListener = new MatchListener(pvpEvent, this);
        this.playerKit = pvpEvent.getKitManager().getIfPresent(arena.getKitName());
    }

    public boolean hasParticipant(UUID identifier) {
        return participantsByUUD.containsKey(identifier);
    }

    @Override
    public void join(UUID identifier) {
        Player player = Bukkit.getServer().getPlayer(identifier);
        if (state == MatchState.QUEUE) {
            if (!hasParticipant(identifier) && player != null) {
                Participant participant = new Participant(player);
                Bukkit.getPluginManager().callEvent(new MatchJoinEvent(this, participant));
                participant.resetThingsOfPlayer();
                participant.getPlayer().teleport(arena.getLobby());

                participantsByUUD.put(identifier, participant);
                statusByUUID.put(identifier, ParticipantStatus.QUEUE);
                player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_JOIN_SUCCESS));
            } else {
                player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_JOIN_JOINED));
            }
        } else {
            player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_JOIN_CANNOT));
        }
    }

    @Override
    public void leave(UUID identifier) {
        if (hasParticipant(identifier)) {
            Participant participant = participantsByUUD.get(identifier);
            Bukkit.getPluginManager().callEvent(new MatchLeaveEvent(this, participant));
            participant.setOriginalsOfPlayer();

            if (statusByUUID.get(identifier) == ParticipantStatus.FIGHTING) {
                String text = Settings.IMP.MESSAGE.BASE_LEAVE_BROADCAST
                        .replace("{user}", participant.getName());
                BukkitHelper.broadcast(text);
                nextRound();
            }

            participantsByUUD.remove(identifier);
            statusByUUID.remove(identifier);
            participant.getPlayer().sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_LEAVE_SUCCESS));
        } else {
            Player player = Bukkit.getServer().getPlayer(identifier);
            player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_LEAVE_CANNOT));
        }
    }

    @Override
    public void spectate(UUID identifier) {
        Player player = Bukkit.getServer().getPlayer(identifier);
        if (!hasParticipant(identifier) && player != null) {
            Participant participant = new Participant(player);
            Bukkit.getPluginManager().callEvent(new MatchJoinEvent(this, participant));
            participantsByUUD.put(identifier, participant);
            statusByUUID.put(identifier, ParticipantStatus.SPECTATE);
            player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_SPECTATE_SUCCESS));
        } else {
            player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_SPECTATE_JOINED));
        }
    }

    @Override
    public void onDeath(UUID identifier, PlayerDeathEvent event) {
        if (statusByUUID.get(identifier) == ParticipantStatus.FIGHTING) {
            Participant victim = participantsByUUD.get(identifier);
            event.getDrops().clear();
            event.setDeathMessage(null);

            statusByUUID.put(identifier, ParticipantStatus.SPECTATE);
            Participant winner = getFighting().get(0);
            winner.getPlayer().teleport(arena.getLobby());
            statusByUUID.put(winner.getIdentifier(), ParticipantStatus.QUEUE);

            String text = Settings.IMP.MESSAGE.MATCH_DEATH_TEXT
                    .replace("{victim}", victim.getName())
                    .replace("{winner}", winner.getName());
            BukkitHelper.broadcast(text);

            nextRound();
            Bukkit.getScheduler().runTaskLater(pvpEvent.getPlugin(), () -> {
                victim.getPlayer().spigot().respawn();
                victim.getPlayer().teleport(arena.getLobby());
            }, 10L);
        }
    }

    @Override
    public Match onCreate(PvPEvent pvpEvent, int modulo) {
        this.pvpEvent = pvpEvent;

        this.state = MatchState.QUEUE;
        this.matchListener.load();

        this.task = syncTaskFactory(arena.getQueueCountdown(), modulo,
                times -> {
                    String text = Settings.IMP.MESSAGE.COUNTDOWN_QUEUE
                            .replace("{second}", String.valueOf(times))
                            .replace("{present}", present)
                            .replace("{arena}", arena.getIdentifier())
                            .replace("{kit}", arena.getKitName());
                    BukkitHelper.broadcastClickable(text);
                }, this::onStartTask);

        return this;
    }

    @Override
    public void onStartTask() {
        this.state = MatchState.ACTIVE;
        Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));

        this.task = syncTaskFactory(3, 1, times -> {
            String text = Settings.IMP.MESSAGE.COUNTDOWN_START
                    .replace("{second}", String.valueOf(times))
                    .replace("{present}", present)
                    .replace("{arena}", arena.getIdentifier())
                    .replace("{kit}", arena.getKitName());
            BukkitHelper.broadcast(text);
        }, this::nextRound);
    }

    protected BukkitTask syncTaskFactory(final long times, final long countdownModulo, Consumer<Long> counter, Runnable executor) {
        return new BukkitRunnable() {

            private long lastTransaction = 0;
            private long currentTimes = times;

            @Override
            public void run() {
                long tick = System.currentTimeMillis();
                if (tick - this.lastTransaction >= (2 << 9)) {
                    this.lastTransaction = tick;
                    if (currentTimes % countdownModulo == 0) {
                        counter.accept(currentTimes);
                    }
                    currentTimes--;
                }
                if (currentTimes <= 0) {
                    executor.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(pvpEvent.getPlugin(), 0L, 1L);
    }

    @Override
    public void onDestroy() {
        Bukkit.getPluginManager().callEvent(new MatchDestroyEvent(this));
        onStopTask();
        this.state = MatchState.INACTIVE;
        this.matchListener.unload();
        this.pvpEvent = null;
    }

    @Override
    public void onStopTask() {
        if (this.task != null) {
            this.task.cancel();
        }
    }

    @Override
    public void onPause() {
        this.state = MatchState.PAUSE;
    }

    @Override
    public abstract void nextRound();

    public List<Participant> getFighting() {
        // Kiválasztjuk azokat a felhasználókat, akik a FIGHTING állapotban vannak
        return statusByUUID.entrySet().stream()
                .filter(entry -> entry.getValue() == ParticipantStatus.FIGHTING)
                .map(entry -> participantsByUUD.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<Participant> getQueue() {
        // Kiválasztjuk azokat a felhasználókat, akik a QUEUE állapotban vannak
        return statusByUUID.entrySet().stream()
                .filter(entry -> entry.getValue() == ParticipantStatus.QUEUE)
                .map(entry -> participantsByUUD.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public MatchState getState() {
        return state;
    }

    @Override
    public void setState(MatchState state) {
        this.state = state;
    }

    public Arena getArena() {
        return arena;
    }

    public Kit<Player> getPlayerKit() {
        return playerKit;
    }

    @Override
    public Map<UUID, Participant> getParticipantsByUUD() {
        return participantsByUUD;
    }

    @Override
    public Map<UUID, ParticipantStatus> getStatusByUUID() {
        return statusByUUID;
    }
}
