package xyz.dysaido.onevsonegame.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.match.model.MatchPlayer;
import xyz.dysaido.onevsonegame.match.model.PlayerState;

public class MatchListener implements Listener {

    private final OneVSOneGame plugin;

    public MatchListener(OneVSOneGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            match.leave(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            match.leave(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void onPlayerInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            MatchPlayer matchPlayer = match.getQueue().findQueuedByPlayer(player);
            if (matchPlayer != null && (matchPlayer.getState() == PlayerState.QUEUE || matchPlayer.getState() == PlayerState.SPECTATOR)) {
                event.setCancelled(true);
            }
        });
    }

}
