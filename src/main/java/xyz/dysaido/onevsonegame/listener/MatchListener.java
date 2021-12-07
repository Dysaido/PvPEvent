package xyz.dysaido.onevsonegame.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.event.GamePlayerLoseEvent;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.Arrays;
import java.util.List;

public class MatchListener implements Listener {

    private static final String TAG = "Listener";
    private final OneVSOneGame plugin;
    private final List<String> commands = Arrays.asList("event", "event:event");

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
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Logger.debug(TAG, "PlayerQuit");
            match.leave(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Logger.debug(TAG, "PlayerKick");
            match.leave(event.getPlayer());
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Player player = event.getPlayer();
            if (match.getQueue().contains(player)) {
                String message = event.getMessage().split(" ")[0].toLowerCase();
                String command = message.substring(1);
                Logger.debug(TAG, "CommandPreprocess, Message: " + message);
                if (!player.hasPermission("event.command.perform") && message.startsWith("/") && !commands.contains(command)) {
                    event.setCancelled(true);
                    player.sendMessage("/event leave");
                }
            }
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Player victim = event.getEntity();
            if (match.getQueue().contains(victim)) {
                MatchPlayer matchPlayer = match.getQueue().findByPlayer(victim);
                Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLoseEvent(match, matchPlayer));
                event.getDrops().clear();
                event.setDeathMessage(null);
                Logger.debug(TAG, "PlayerDeath, MatchPlayer: " + matchPlayer.getPlayer().getName());
                matchPlayer.reset(match.getRing().getLobby(), PlayerState.SPECTATOR);
            }
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Player player = (Player) event.getWhoClicked();
            if (match.getQueue().contains(player)) {
                MatchPlayer matchPlayer = match.getQueue().findByPlayer(player);
                if (Settings.INVENTORY_FREEZE && matchPlayer.getState() == PlayerState.QUEUE || matchPlayer.getState() == PlayerState.SPECTATOR) {
                    Logger.debug(TAG, "InventoryClick - Queued player do not authorized to use own inventory");
                    event.setCancelled(true);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Player player = event.getPlayer();
            if (match.getQueue().contains(player)) {
                MatchPlayer matchPlayer = match.getQueue().findByPlayer(player);
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
