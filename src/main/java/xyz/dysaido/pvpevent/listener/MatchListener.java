package xyz.dysaido.onevsonegame.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MatchListener implements Listener {

    private static final String TAG = "Listener";
    private final OneVSOneGame plugin;
    private static final List<String> WHITELISTED_COMMANDS = Arrays.asList("event", "event:event");

    public MatchListener(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && plugin.hasNewVersion()) {
            player.sendMessage(Format.colored("&3Update available, please check spigot website!"));
            player.sendMessage(Format.colored("&3Here: &1&ohttps://www.spigotmc.org/resources/1v1-event.97786/"));
        }
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

    @EventHandler()
    public void protectQueuedPlayers(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();
        Entity attackerEntity = event.getDamager();
        if (victimEntity instanceof Player) {
            Player victim = (Player) victimEntity;
            plugin.getMatchHandler().getMatch().ifPresent(match -> {
                if (match.getQueue().containsQueue(victim)) {
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler()
    public void protectQueuedPlayers(ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        if (entity.getType() != EntityType.ENDER_PEARL) return;

        if (entity.getShooter() instanceof Player) {
            Player player = (Player) entity.getShooter();
            plugin.getMatchHandler().getMatch().ifPresent(match -> {
                if (match.getQueue().containsQueue(player)) {
                    event.setCancelled(true);
                }
            });
        }

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Player player = event.getPlayer();
            if (match.getQueue().containsQueue(player) || match.getQueue().containsFighter(player)) {
                String message = event.getMessage().split(" ")[0].toLowerCase();
                Logger.debug(TAG, "CommandPreprocess, Message: " + message);
                if (!player.hasPermission("event.command.perform") && isCommandBlacklisted(message)) {
                    event.setCancelled(true);
                    player.sendMessage("/event leave");
                }
            }
        });
    }

    public boolean isCommandBlacklisted(String message) {
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
            if (match.getQueue().containsFighter(victim)) {
                MatchPlayer matchPlayer = match.getQueue().findFighterByName(victim.getName());
                event.getDrops().clear();
                event.setDeathMessage(null);
                Logger.debug(TAG, "PlayerDeath, MatchPlayer: " + matchPlayer.getPlayer().getName());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    victim.spigot().respawn();
                    matchPlayer.reset(match.getArena().getLobby(), true);
                }, 10L);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getMatchHandler().getMatch().ifPresent(match -> {
            Player player = (Player) event.getWhoClicked();
            if (match.getQueue().containsQueue(player)) {
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
            if (match.getQueue().containsFighter(player)) {
                MatchPlayer matchPlayer = match.getQueue().findFighterByName(player.getName());
                if (matchPlayer.isFrozen() && hasMove(event.getFrom(), event.getTo())) {
                    Logger.debug(TAG, String.format("PlayerMove - %s was frozen", matchPlayer.getPlayer().getName()));
                    event.setTo(event.getFrom());
                }
            }
        });
    }

    public boolean hasMove(Location from, Location to) {
        return to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ();
    }
}
