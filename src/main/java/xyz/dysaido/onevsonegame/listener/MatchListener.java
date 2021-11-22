package xyz.dysaido.onevsonegame.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import xyz.dysaido.inventory.DyInventory;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.event.GamePlayerLoseEvent;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MatchListener implements Listener {

    private final OneVSOneGame plugin;
    private final List<String> commands = Arrays.asList("event", "event:event");
    public MatchListener(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> match.leave(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> match.leave(event.getPlayer()));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Player player = event.getPlayer();
            if (match.getQueue().contains(player)) {
                String message = event.getMessage().split(" ")[0].toLowerCase();
                String command = message.substring(1);
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
                matchPlayer.reset(match.getRing().getLobby(), PlayerState.SPECTATOR);
            }
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory() != null ? event.getClickedInventory() : event.getInventory();
        Logger.debug("Listener", "OnClick");
        if (inventory != null && inventory.getHolder() instanceof DyInventory) {
            Logger.debug("Listener", "inventory");
            ((DyInventory) inventory.getHolder()).onClick(event);
            event.setCancelled(true);
        } else if (event.getView().getTopInventory().getHolder() instanceof DyInventory) {
            Logger.debug("Listener", "topInventory");
            event.setCancelled(true);
        }
        Logger.debug("Listener", "Else");
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            Player player = (Player) event.getWhoClicked();
            if (match.getQueue().contains(player)) {
                MatchPlayer matchPlayer = match.getQueue().findByPlayer(player);
                if (matchPlayer.getState() == PlayerState.QUEUE || matchPlayer.getState() == PlayerState.SPECTATOR) {
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
                    event.setTo(event.getFrom());
                }
            }
        });
    }

    public boolean hasMove(Location from, Location to) {
        return to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ();
    }
}
