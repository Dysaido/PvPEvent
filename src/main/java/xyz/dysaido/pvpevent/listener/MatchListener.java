package xyz.dysaido.pvpevent.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.match.BaseMatch;
import xyz.dysaido.pvpevent.match.model.Participant;
import xyz.dysaido.pvpevent.setting.Settings;
import xyz.dysaido.pvpevent.util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MatchListener implements Listener {

    private static final String TAG = "Listener";
    private final PvPEvent plugin;
    private static final List<String> WHITELISTED_COMMANDS = Arrays.asList("event", "event:event");

    public MatchListener(PvPEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Logger.debug(TAG, "PlayerQuit");
            match.leave(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Logger.debug(TAG, "PlayerKick");
            match.leave(event.getPlayer());
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Player player = event.getPlayer();
            if (match.getQueue().contains(player)) {
                String message = event.getMessage().split(" ")[0].toLowerCase();
                if (!player.hasPermission("event.command.perform") && isCommandWhitelisted(message)) {
                    event.setCancelled(true);
                    player.sendMessage("/event leave");
                }
            }
        });
    }

    public boolean isCommandWhitelisted(String message) {
        if (message.charAt(0) == '/') {
            message = message.substring(1);
        }

        message = message.toLowerCase();

        for (String command : WHITELISTED_COMMANDS) {
            if (message.equals(command) || message.startsWith(command + " ")) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Optional<BaseMatch> optionalMatch = plugin.getMatchHandler().getMatch();
        if (optionalMatch.isPresent()) {
            BaseMatch match = optionalMatch.get();
            Player victim = event.getEntity();
            if (match.getQueue().contains(victim)) {
                Participant participant = match.getQueue().findParticipantByName(victim.getName());
                if (participant.getState().equals(Participant.State.FIGHT)) {
                    event.getDrops().clear();
                    event.setDeathMessage(null);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        victim.spigot().respawn();
                        participant.reset(match.getArena().getLobby(), true);
                    }, 10L);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Player player = (Player) event.getWhoClicked();
            if (match.getQueue().contains(player)) {
                if (Settings.INVENTORY_FREEZE) {
                    Logger.debug(TAG, "InventoryClick - Queued player do not authorized to use own inventory");
                    event.setCancelled(true);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Player player = event.getPlayer();
            if (match.getQueue().contains(player)) {
                Participant participant = match.getQueue().findParticipantByName(player.getName());
                if (participant.isFrozen() && hasMove(event.getFrom(), event.getTo())) {
                    Logger.debug(TAG, String.format("PlayerMove - %s was frozen", participant.getPlayer().getName()));
                    event.setTo(event.getFrom());
                }
            }
        });
    }

    public boolean hasMove(Location from, Location to) {
        return to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ();
    }
}
