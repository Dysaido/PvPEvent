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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import xyz.dysaido.pvpevent.api.pagination.ItemBuilder;
import xyz.dysaido.pvpevent.api.pagination.Materials;
import xyz.dysaido.pvpevent.config.Settings;

import java.util.Collection;
import java.util.UUID;

public class Participant {

    private final UUID id;
    private final String name;
    private final Player player;
    private final AbstractMatch match;
    private final Scoreboard originalScoreboard;
    private final Location originalLocation;
    private final Collection<PotionEffect> originalPotionEffects;
    private final int originalFireTicks;
    private final float originalWalkSpeed;
    private final GameMode originalGamemode;
    private final Kit<Player> originalInventory;
    private final int originalNoDamageTicks;
    private final int originalLevel;
    private final float originalExp;
    private boolean freeze;
    public Participant(Player player, AbstractMatch match) {
        this.player = player;
        this.match = match;
        this.id = player.getUniqueId();
        this.name = player.getName();
        this.originalScoreboard = player.getScoreboard();
        this.originalLocation = player.getLocation();
        this.originalPotionEffects = player.getActivePotionEffects();
        this.originalFireTicks = player.getFireTicks();
        this.originalWalkSpeed = player.getWalkSpeed();
        this.originalGamemode = player.getGameMode();
        this.originalLevel = player.getLevel();
        this.originalExp = player.getExp();

        this.originalInventory = new Kit<>(String.format("OriginalKit:%s", player.getName()));
        this.originalInventory.setArmor(player.getInventory().getArmorContents());
        this.originalInventory.setContents(player.getInventory().getContents());

        this.originalNoDamageTicks = player.getMaximumNoDamageTicks();

        if (match.getArena().isComboMode()) {
            player.setMaximumNoDamageTicks(3);
        }
    }

    public void resetThingsOfPlayer() {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setWalkSpeed(0.2f);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        Settings.GUI guiSets = Settings.IMP.GUI;
        ItemBuilder item = new ItemBuilder(Materials.CLOCK.asBukkit()).displayName(guiSets.QUEUE_LEAVE_NAME);
        int slot = guiSets.QUEUE_LEAVE_SLOTBAR > 0 ? Math.min(8, guiSets.QUEUE_LEAVE_SLOTBAR) : 0;
        player.getInventory().setItem(slot, item.build());
        player.updateInventory();
    }

    public void setOriginalsOfPlayer() {
        originalInventory.accept(player);
        player.setMaximumNoDamageTicks(originalNoDamageTicks);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(originalFireTicks);
        player.setWalkSpeed(originalWalkSpeed);
        player.setLevel(originalLevel);
        player.setExp(originalExp);
        player.setGameMode(originalGamemode);

        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        originalPotionEffects.forEach(player::addPotionEffect);

        player.teleport(originalLocation);
        player.setScoreboard(originalScoreboard);
    }
    public Player getPlayer() {
        return player;
    }

    public Scoreboard getOriginalScoreboard() {
        return originalScoreboard;
    }

    public Location getOriginalLocation() {
        return originalLocation;
    }

    public Collection<PotionEffect> getOriginalPotionEffects() {
        return originalPotionEffects;
    }

    @Deprecated
    public double getOriginalHealth() {
        return 20;
    }

    @Deprecated
    public int getOriginalFoodLevel() {
        return 20;
    }

    public int getOriginalFireTicks() {
        return originalFireTicks;
    }

    public float getOriginalWalkSpeed() {
        return originalWalkSpeed;
    }

    public GameMode getOriginalGamemode() {
        return originalGamemode;
    }

    public Kit<Player> getOriginalInventory() {
        return originalInventory;
    }

    public UUID getIdentifier() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
        this.player.setWalkSpeed(freeze ? 0f : 0.2f);
    }
}
