package xyz.dysaido.pvpevent.match.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.api.event.impl.DuelNextRoundEvent;
import xyz.dysaido.pvpevent.api.event.sumo.SumoNextRoundEvent;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.util.BukkitHelper;

import java.util.List;
import java.util.UUID;

public class DuelMatch extends AbstractMatch {

    protected int round = 0;

    public DuelMatch(PvPEventPlugin pvpEvent, String present, Arena arena) {
        super(pvpEvent, present, arena);
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
            victim.getPlayer().setHealth(victim.getPlayer().getMaxHealth());
            victim.getPlayer().setFoodLevel(20);
            victim.getPlayer().teleport(arena.getLobby());
        }
    }

    @Override
    public void nextRound() {
        List<Participant> queueUsers = getQueue();

        int numUsers = queueUsers.size();
        if (numUsers >= 2) {
            // Itt implementálhatjuk az ellenfelek kiválasztását, például véletlenszerűen:
            Participant user1 = queueUsers.get((int) (Math.random() * numUsers));
            Participant user2 = queueUsers.get((int) (Math.random() * numUsers));
            while (user1.equals(user2)) {
                // Győzőrünk, hogy a két kiválasztott felhasználó ne legyen azonos
                user2 = queueUsers.get((int) (Math.random() * numUsers));
            }

            // Felhasználók státuszának frissítése a küzdelemre állításukhoz
            statusByUUID.put(user1.getIdentifier(), ParticipantStatus.FIGHTING);
            statusByUUID.put(user2.getIdentifier(), ParticipantStatus.FIGHTING);

            this.round++;
            Bukkit.getPluginManager().callEvent(new SumoNextRoundEvent(this, round, user1, user2));
            Bukkit.getPluginManager().callEvent(new DuelNextRoundEvent(this, round, user1, user2));

            user1.getPlayer().teleport(arena.getPos1());
            user1.setFreeze(true);
            user2.getPlayer().teleport(arena.getPos2());
            user2.setFreeze(true);
            if (playerKit != null) {
                playerKit.accept(user1.getPlayer());
                playerKit.accept(user2.getPlayer());
            }
            Runnable runnable = getRunnable(user1, user2);
            int freezeTimes = arena.getFightCountdown();
            if (freezeTimes < 3) {
                runnable.run();
            } else {
                int modulo = Math.max(Settings.IMP.COUNTDOWN.SUMO_NEXTROUND_MODULO, 1);
                this.task = syncTaskFactory(freezeTimes, modulo,
                        times -> {

                            String text = Settings.IMP.MESSAGE.SUMO_NEXTROUND_COUNTDOWN_TEXT
                                    .replace("{second}", String.valueOf(times));
                            BukkitHelper.broadcast(text);
                        }, runnable);
            }
        } else {
            if (!queueUsers.isEmpty() && round != 0) {
                Participant participant = queueUsers.get(0);
                String text = Settings.IMP.MESSAGE.SUMO_NEXTROUND_WITH_WINNER_TEXT
                        .replace("{user}", participant.getName())
                        .replace("{present}", present);
                BukkitHelper.broadcast(text);
            } else {
                String text = Settings.IMP.MESSAGE.SUMO_NEXTROUND_WITHOUT_WINNER_TEXT
                        .replace("{present}", present);
                BukkitHelper.broadcast(text);
            }
            onDestroy();
        }
    }

    private Runnable getRunnable(Participant user1, Participant user2) {
        return () -> {
            user1.setFreeze(false);
            user2.setFreeze(false);

            String text = Settings.IMP.MESSAGE.SUMO_NEXTROUND_SUCCESS_TEXT
                    .replace("{round}", String.valueOf(round))
                    .replace("{user1}", user1.getName())
                    .replace("{user2}", user2.getName());
            BukkitHelper.broadcast(text);
        };
    }
}
