/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.dysaido.pvpevent.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
import xyz.dysaido.pvpevent.util.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractMatch implements Match<UUID> {

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

        this.participantsByUUD = new ConcurrentHashMap<>();
        this.statusByUUID = new ConcurrentHashMap<>();
        
        this.matchListener = new MatchListener(pvpEvent, this);
        this.playerKit = pvpEvent.getKitManager().getIfPresent(arena.getKitName());
    }

    @Override
    public boolean hasParticipant(UUID identifier) {
        return participantsByUUD.containsKey(identifier);
    }

    @Override
    public ParticipantStatus getParticipantStatus(UUID identifier) {
        return statusByUUID.get(identifier);
    }

    @Override
    public void join(UUID identifier) {
        Player player = Bukkit.getServer().getPlayer(identifier);
        if (player != null) {
            if (state == MatchState.QUEUE && !isFull()) {
                if (!hasParticipant(identifier)) {
                    Participant participant = new Participant(player, this);
                    Bukkit.getPluginManager().callEvent(new MatchJoinEvent(this, participant, ParticipantStatus.QUEUE));
                    participant.getPlayer().teleport(arena.getLobby().asBukkit(true));
                    participant.resetThingsOfPlayer();

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
        if (player != null) {
            if (!hasParticipant(identifier)) {
                Participant participant = new Participant(player, this);
                Bukkit.getPluginManager().callEvent(new MatchJoinEvent(this, participant, ParticipantStatus.SPECTATE));
                participantsByUUD.put(identifier, participant);
                statusByUUID.put(identifier, ParticipantStatus.SPECTATE);
                player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_SPECTATE_SUCCESS));
            } else {
                player.sendMessage(BukkitHelper.colorize(Settings.IMP.MESSAGE.BASE_SPECTATE_JOINED));
            }
        }
    }

    @Override
    public Match<UUID> onCreate(PvPEvent pvpEvent, List<Integer> announcements) {
        this.pvpEvent = pvpEvent;

        this.state = MatchState.QUEUE;
        this.matchListener.load();

        int arenaQueueTimes = Math.max(arena.getQueueCountdown(), 10);

        Location lobby = this.arena.getLobby().asBukkit(true);
        World world = lobby.getWorld();
        int cx = floor(lobby.getX()) >> 4; // = lobby.getBlockX() >> 4;
        int cz = floor(lobby.getZ()) >> 4; // = lobby.getBlockZ() >> 4;
        Logger.debug("AbstractMatch", String.format("ChunkPos(x=%d, z=%d) - isLoaded:%b", cx, cz, world.isChunkLoaded(cx, cz)));
        /*if (!world.isChunkLoaded(cx, cz)) {
            //  /\
            //  ||
            // shit 
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    world.getChunkAt(cx + dx, cz + dz);
                    Logger.debug("AbstractMatch", String.format("ChunkPreloader(x=%d, z=%d)", cx + dx, cz + dz));
                }
            }
        }*/
        
        this.task = syncTaskFactory(arenaQueueTimes, announcements,
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
    
    public static int floor(double num) {
        int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    @Override
    public void onStartTask() {
        Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
        this.state = MatchState.ACTIVE;

        if (isInsufficient()) {
            BukkitHelper.broadcast(Settings.IMP.MESSAGE.MATCH_STOP_TEXT.replace("{executor}", "CONSOLE"));
            
            String text = Settings.IMP.MESSAGE.SUMO_NEXTROUND_WITHOUT_WINNER_TEXT
                    .replace("{present}", present);
            BukkitHelper.broadcast(text);
            onDestroy();
            return;
        }

        int cnfTimes = Settings.IMP.COUNTDOWN.BASE_START_TIMES;
        if (cnfTimes < 3) {
            String text = Settings.IMP.MESSAGE.COUNTDOWN_START
                    .replace("{second}", String.valueOf(1))
                    .replace("{present}", present)
                    .replace("{arena}", arena.getIdentifier())
                    .replace("{kit}", arena.getKitName());
            BukkitHelper.broadcast(text);
            nextRound();
        } else {
            this.task = syncTaskFactory(cnfTimes, Settings.IMP.COUNTDOWN.BASE_START_ANNOUNCE, times -> {
                String text = Settings.IMP.MESSAGE.COUNTDOWN_START
                        .replace("{second}", String.valueOf(times))
                        .replace("{present}", present)
                        .replace("{arena}", arena.getIdentifier())
                        .replace("{kit}", arena.getKitName());
                BukkitHelper.broadcast(text);
            }, this::nextRound);
        }
    }

    @Override
    public void onDestroy() {
        Bukkit.getPluginManager().callEvent(new MatchDestroyEvent(this));
        this.state = MatchState.INACTIVE;

        onStopTask();

        this.participantsByUUD.values().forEach(Participant::setOriginalsOfPlayer);
        this.participantsByUUD.clear();
        this.statusByUUID.clear();
        this.matchListener.unload();
    }

    protected BukkitTask syncTaskFactory(final int times, final List<Integer> announcements, Consumer<Integer> counter, Runnable executor) {
        return new BukkitRunnable() {

            private long lastTransaction = 0;
            private int currentTimes = times;

            @Override
            public void run() {
                long tick = System.currentTimeMillis();
                if (tick - this.lastTransaction >= (2 << 9)) {
                    this.lastTransaction = tick;
                    if (announcements.contains(currentTimes)) {
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
    public boolean isOver() {
        return state == MatchState.INACTIVE;
    }

    @Override
    public boolean isFull() {
        return this.participantsByUUD.size() > arena.getCapacity();
    }

    @Override
    public boolean isInsufficient() {
        return this.participantsByUUD.size() < arena.getMinCapacity();
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
