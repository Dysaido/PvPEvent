package xyz.dysaido.pvpevent.model;

import org.bukkit.Location;
import xyz.dysaido.pvpevent.api.model.Model;

public class Arena implements Model<String, Arena> {

    private final String identifier;
    private Location lobby;
    private Location pos1;
    private Location pos2;
    private String kitName;
    private int capacity = 30;
    private int queueCountdown = 10;
    private int fightCountdown = 5;

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

    public boolean isValidPositions() {
        return pos1 != null && pos2 != null;
    }

    public boolean isValidKit() {
        return kitName != null;
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

    public void setKit(String kitName) {
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

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }
}
