package xyz.dysaido.pvpevent.model;

import org.bukkit.Location;
import xyz.dysaido.pvpevent.api.model.Model;
import xyz.dysaido.pvpevent.util.CustomLocation;

public class Arena implements Model<String, Arena> {

    private final String identifier;
    private CustomLocation lobby;
    private CustomLocation pos1;
    private CustomLocation pos2;
    private CustomLocation min;
    private CustomLocation max;
    private String kitName = "";
    private int minCapacity = 2;
    private int capacity = 30;
    private int queueCountdown = 10;
    private int fightCountdown = 3;
    private boolean toggleInventory = false;

    public Arena(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Arena getOwner() {
        return this;
    }

    public boolean shouldTeleport() {
        return lobby != null && (pos1 != null || pos2 != null);
    }

    public boolean shouldApplyKit() {
        return kitName != null && kitName.isEmpty();
    }

    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setQueueCountdown(int queueCountdown) {
        this.queueCountdown = queueCountdown;
    }
    public int getQueueCountdown() {
        return queueCountdown;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public String getKitName() {
        return kitName;
    }

    public void setFightCountdown(int fightCountdown) {
        this.fightCountdown = fightCountdown;
    }

    public int getFightCountdown() {
        return fightCountdown;
    }

    public void setPos1(CustomLocation pos1) {
        this.pos1 = pos1;
    }

    public CustomLocation getPos1() {
        return pos1;
    }

    public void setPos2(CustomLocation pos2) {
        this.pos2 = pos2;
    }

    public CustomLocation getPos2() {
        return pos2;
    }

    public void setLobby(CustomLocation lobby) {
        this.lobby = lobby;
    }

    public CustomLocation getLobby() {
        return lobby;
    }

    public void setToggleInventory(boolean toggleInventory) {
        this.toggleInventory = toggleInventory;
    }

    public boolean isToggleInventory() {
        return toggleInventory;
    }
}
