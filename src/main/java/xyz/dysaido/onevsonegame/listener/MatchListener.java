package xyz.dysaido.onevsonegame.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> match.leave(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.getMatchManager().getMatch().ifPresent(match -> match.leave(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            MatchPlayer matchPlayer = match.getQueue().findByPlayer(victim);
            if (matchPlayer != null) {
                event.setDeathMessage(null);
                event.getDrops().clear();
                matchPlayer.reset(match.getRing().getLobby(), PlayerState.SPECTATOR);
            }
        });
    }

    @EventHandler
    public void onPlayerInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            MatchPlayer matchPlayer = match.getQueue().findByPlayer(player);
            if (matchPlayer != null && (matchPlayer.getState() == PlayerState.QUEUE || matchPlayer.getState() == PlayerState.SPECTATOR)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        plugin.getMatchManager().getMatch().ifPresent(match -> {
            MatchPlayer matchPlayer = match.getQueue().findByPlayer(player);
            if (matchPlayer != null && matchPlayer.isFrozen() && hasMove(event.getFrom(), event.getTo())) {
                event.setTo(event.getFrom());
            }
        });
    }

    public boolean hasMove(Location from, Location to) {
        return to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ();
    }
}
