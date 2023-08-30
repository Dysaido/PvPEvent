package xyz.dysaido.pvpevent.match.impl;

import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.api.event.impl.DuelDeathEvent;
import xyz.dysaido.pvpevent.api.event.impl.DuelNextRoundEvent;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.model.manager.UserManager;
import xyz.dysaido.pvpevent.util.BukkitHelper;

import java.util.List;
import java.util.UUID;

public class DuelMatch extends AbstractMatch {

    protected int round = 0;
    protected UserManager userManager;

    public DuelMatch(PvPEventPlugin pvpEvent, String present, Arena arena) {
        super(pvpEvent, present, arena);
        userManager = pvpEvent.getUserManager();
    }

    @Override
    public void onDeath(UUID identifier, PlayerDeathEvent event) {
        if (statusByUUID.get(identifier) == ParticipantStatus.FIGHTING) {
            Participant victim = participantsByUUD.get(identifier);
            victim.getPlayer().setHealth(victim.getPlayer().getMaxHealth());
            victim.getPlayer().setFoodLevel(20);
            victim.getPlayer().teleport(arena.getLobby().asBukkit(true));
            statusByUUID.put(identifier, ParticipantStatus.SPECTATE);

            event.getDrops().clear();
            event.setDeathMessage(null);

            Participant winner = getFighting().get(0);
            winner.getPlayer().teleport(arena.getLobby().asBukkit(true));
            statusByUUID.put(winner.getIdentifier(), ParticipantStatus.QUEUE);

            String text = Settings.IMP.MESSAGE.MATCH_DEATH_TEXT
                    .replace("{victim}", victim.getName())
                    .replace("{winner}", winner.getName());

            Bukkit.getPluginManager().callEvent(new DuelDeathEvent(this, round, victim, winner, text));

            BukkitHelper.broadcast(text);

            userManager.withAction(identifier).then(user -> {
                user.setName(victim.getName());
                user.addDeath();
                userManager.getSerializer().append(user);
            });

            userManager.withAction(winner.getIdentifier()).then(user -> {
                user.setName(winner.getName());
                user.addKill();
                userManager.getSerializer().append(user);
            });

            nextRound();
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
            Bukkit.getPluginManager().callEvent(new DuelNextRoundEvent(this, round, user1, user2));

            if (arena.getPos1() != null) {
                if (arena.getPos2() != null) {
                    user1.getPlayer().teleport(arena.getPos1().asBukkit(true));
                    user2.getPlayer().teleport(arena.getPos2().asBukkit(true));
                } else {
                    user1.getPlayer().teleport(arena.getPos1().asBukkit(true));
                    user2.getPlayer().teleport(arena.getPos1().asBukkit(true));
                }
            } else {
                user1.getPlayer().teleport(arena.getPos2().asBukkit(true));
                user2.getPlayer().teleport(arena.getPos2().asBukkit(true));
            }

            user1.setFreeze(true);
            user2.setFreeze(true);
            if (arena.shouldApplyKit() && !arena.isToggleInventory()) {
                playerKit.accept(user1.getPlayer());
                playerKit.accept(user2.getPlayer());
            } else if (arena.isToggleInventory()){
                user1.getOriginalInventory().accept(user1.getPlayer());
                user2.getOriginalInventory().accept(user2.getPlayer());
            }

            Runnable runnable = getRunnable(user1, user2);
            int freezeTimes = arena.getFightCountdown();
            if (freezeTimes < 3) {
                runnable.run();
            } else {
                this.task = syncTaskFactory(freezeTimes, Settings.IMP.COUNTDOWN.BASE_NEXTROUND_ANNOUNCE,
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

                userManager.withAction(participant.getIdentifier()).then(user -> {
                   user.setName(participant.getName());
                   user.addWin();
                   userManager.getSerializer().append(user);
                });

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
