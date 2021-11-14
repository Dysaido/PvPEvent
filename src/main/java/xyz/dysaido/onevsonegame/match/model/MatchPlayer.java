package xyz.dysaido.onevsonegame.match.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import xyz.dysaido.onevsonegame.match.Match;

import java.util.Collection;
import java.util.UUID;

public class MatchPlayer {

    private final ItemStack[] originalContents;
    private final ItemStack[] originalArmor;

    private final Collection<PotionEffect> originalPotionEffects;
    private final double originalHealth;
    private final int originalFoodLevel;
    private final int originalFireTicks;
    private final Player player;
    private final Match match;
    private PlayerState state = PlayerState.QUEUE;
    private boolean lose = false;

    public MatchPlayer(Match match, Player player) {
        this.match = match;
        this.player = player;
        this.originalPotionEffects = player.getActivePotionEffects();
        this.originalHealth = player.getHealth();
        this.originalFoodLevel = player.getFoodLevel();
        this.originalFireTicks = player.getFireTicks();
        this.originalContents = player.getInventory().getContents();
        this.originalArmor = player.getInventory().getArmorContents();
        player.teleport(match.getRing().getLobby());
    }

    public void setup(Location location) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        match.getRing().getKit().apply(player);
        player.teleport(location);
        match.getQueue().addFight(this);
    }

    public void freeze() {

    }

    public void unfreeze() {

    }

    public void reset(Location location) {
        player.setHealth(originalHealth);
        player.setFoodLevel(originalFoodLevel);
        player.setFireTicks(originalFireTicks);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        originalPotionEffects.forEach(player::addPotionEffect);
        player.getInventory().setContents(originalContents);
        player.getInventory().setArmorContents(originalArmor);
        player.teleport(location);
        state = lose ? PlayerState.SPECTATOR : PlayerState.QUEUE;
    }


    public UUID id() {
        return player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    public Match getMatch() {
        return match;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
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

    public ItemStack[] getOriginalContents() {
        return originalContents;
    }

    public ItemStack[] getOriginalArmor() {
        return originalArmor;
    }

    public boolean isLose() {
        return lose;
    }

    public void setLose(boolean lose) {
        this.lose = lose;
    }
}
