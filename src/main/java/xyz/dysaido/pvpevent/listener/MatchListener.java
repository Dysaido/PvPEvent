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

package xyz.dysaido.pvpevent.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.api.pagination.Materials;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;
import xyz.dysaido.pvpevent.util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MatchListener extends EventListener {
    private static final String TAG = "Listener";
    private static final List<String> WHITELISTED_COMMANDS = Arrays.asList("event", "pvpevent:event");
    private final AbstractMatch match;
    public MatchListener(PvPEvent pvpEvent, AbstractMatch match) {
        super(pvpEvent);
        this.match = match;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (match.hasParticipant(player.getUniqueId())) {
            match.leave(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (match.hasParticipant(player.getUniqueId())) {
            match.leave(player.getUniqueId());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (match.hasParticipant(player.getUniqueId())) {
            String message = event.getMessage().split(" ")[0].toLowerCase();
            if (!player.hasPermission(Settings.IMP.PERMISSION.COMMAND_PERFORM) && isCommandWhitelisted(message)) {
                event.setCancelled(true);
                player.sendMessage("/event leave");
            }
        }
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
        Player victim = event.getEntity();
        UUID id = victim.getUniqueId();
        if (match.hasParticipant(id)) {
            match.onDeath(id, event);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID id = player.getUniqueId();

        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) != ParticipantStatus.FIGHTING) {
            player.setHealth(20.0);
            event.setCancelled(true);
        }
    }

    /*

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID id = player.getUniqueId();

        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) != ParticipantStatus.FIGHTING) {
            player.setHealth(20.0);
            event.setCancelled(true);
            if (event.getDamager() instanceof Player) {
                event.getDamager().sendMessage("Event kovben nem faszolhatod a masikat");
            }
        }
    }*/

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        UUID id = player.getUniqueId();
        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) != ParticipantStatus.FIGHTING) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID id = player.getUniqueId();
        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) != ParticipantStatus.FIGHTING) {
            Logger.debug(TAG, "InventoryClick - Queued player do not authorized to use own inventory");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) != ParticipantStatus.FIGHTING) {
            Logger.debug(TAG, "PlayerDropItem - Queued player do not authorized to drop any items");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) != ParticipantStatus.FIGHTING) {
            Logger.debug(TAG, "PlayerInteract - Queued player do not authorized to interact");
            event.setCancelled(true);

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack heldItem = player.getInventory().getItemInHand();
                if (heldItem.getType() == Materials.CLOCK.asBukkit()) {
                    match.leave(id);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (match.hasParticipant(id) && match.getStatusByUUID().get(id) == ParticipantStatus.FIGHTING) {
            Participant participant = match.getParticipantsByUUD().get(id);
            if (participant.isFreeze() && hasMove(event.getFrom(), event.getTo())) {
                Logger.debug(TAG, String.format("PlayerMove - %s was frozen", participant.getPlayer().getName()));
                event.setTo(event.getFrom());
            }
        }
    }

    public boolean hasMove(Location from, Location to) {
        return to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ();
    }
}
