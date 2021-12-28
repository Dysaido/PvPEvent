package xyz.dysaido.pvpevent.match.model;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import xyz.dysaido.pvpevent.kit.Kit;
import xyz.dysaido.pvpevent.match.BaseMatch;

import java.util.Collection;
import java.util.UUID;

public class Participant {

    private final Collection<PotionEffect> originalPotionEffects;
    private final double originalHealth;
    private final int originalFoodLevel;
    private final int originalFireTicks;
    private final float originalWalkSpeed;
    private final GameMode originalGamemode;
    private final Kit backupKit;
    private final Player player;
    private final BaseMatch match;
    private volatile State state = State.QUEUE;
    private boolean lose = false;
    private boolean frozen = false;

    public Participant(BaseMatch match, Player player) {
        this.match = match;
        this.player = player;
        this.originalPotionEffects = player.getActivePotionEffects();
        this.originalHealth = player.getHealth();
        this.originalFoodLevel = player.getFoodLevel();
        this.originalFireTicks = player.getFireTicks();
        this.originalWalkSpeed = player.getWalkSpeed();
        this.backupKit = new Kit(player.getInventory().getContents(), player.getInventory().getArmorContents());
        this.originalGamemode = player.getGameMode();
        player.teleport(match.getArena().getLobby());
    }

    public synchronized void setup(Location location) {
        state = State.FIGHT;
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setWalkSpeed(0.2f);
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        match.getArena().getKit().apply(player);
        player.teleport(location);
    }

    public void freeze() {
        frozen = true;
        player.setWalkSpeed(0f);
    }

    public void unfreeze() {
        player.setWalkSpeed(0.2f);
        frozen = false;
    }

    public synchronized void reset(Location location, boolean lose) {
        this.lose = lose;
        if (lose) {
            state = State.SPECTATOR;
        } else {
            state = State.QUEUE;
        }
        player.setHealth(originalHealth);
        player.setFoodLevel(originalFoodLevel);
        player.setFireTicks(originalFireTicks);
        player.setWalkSpeed(originalWalkSpeed);
        player.setGameMode(originalGamemode);
        backupKit.apply(player);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        originalPotionEffects.forEach(player::addPotionEffect);
        player.teleport(location);
    }

    public String getName() {
        return player.getName();
    }

    public UUID id() {
        return player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    public BaseMatch getMatch() {
        return match;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
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

    public float getOriginalWalkSpeed() {
        return originalWalkSpeed;
    }

    public boolean isLose() {
        return lose;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public enum State {
        QUEUE, FIGHT, SPECTATOR, WINNER
    }
}
