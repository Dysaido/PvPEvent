package xyz.dysaido.pvpevent.match;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.UUID;

public class Participant {

    private final UUID id;
    private final String name;
    private final Player player;
    private final Scoreboard originalScoreboard;
    private final Location originalLocation;
    private final Collection<PotionEffect> originalPotionEffects;
    private final double originalHealth;
    private final int originalFoodLevel;
    private final int originalFireTicks;
    private final float originalWalkSpeed;
    private final GameMode originalGamemode;
    private final Kit<Player> originalInventory;
    private boolean freeze;
    public Participant(Player player) {
        this.player = player;
        this.id = player.getUniqueId();
        this.name = player.getName();
        this.originalScoreboard = player.getScoreboard();
        this.originalLocation = player.getLocation();
        this.originalPotionEffects = player.getActivePotionEffects();
        this.originalHealth = player.getHealth();
        this.originalFoodLevel = player.getFoodLevel();
        this.originalFireTicks = player.getFireTicks();
        this.originalWalkSpeed = player.getWalkSpeed();
        this.originalGamemode = player.getGameMode();
        this.originalInventory = new Kit<>(String.format("OriginalKit:%s", player.getName()));

        this.originalInventory.setArmor(player.getInventory().getArmorContents());
        this.originalInventory.setContents(player.getInventory().getContents());
    }

    public void resetThingsOfPlayer() {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setWalkSpeed(0.2f);
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
    }

    public void setOriginalsOfPlayer() {
        player.setHealth(originalHealth);
        player.setFoodLevel(originalFoodLevel);
        player.setFireTicks(originalFireTicks);
        player.setWalkSpeed(originalWalkSpeed);
        player.setGameMode(originalGamemode);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        originalPotionEffects.forEach(player::addPotionEffect);
        player.teleport(originalLocation);

        originalInventory.accept(player);
        player.setScoreboard(originalScoreboard);
    }

    public void freeze() {
        player.setWalkSpeed(0f);
    }

    public void unfreeze() {
        player.setWalkSpeed(0.2f);
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

    public double getOriginalHealth() {
        return originalHealth;
    }

    public int getOriginalFoodLevel() {
        return originalFoodLevel;
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
    }
}
