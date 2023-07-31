package xyz.dysaido.pvpevent.model;

import xyz.dysaido.pvpevent.api.model.Model;

import java.util.UUID;

public class User implements Model<UUID, User> {

    private final UUID identifier;
    private String name;
    private int kills = 0;
    private int deaths = 0;
    private int wins = 0;
    private boolean punished = false;

    public User(UUID uniqueId) {
        this.identifier = uniqueId;
    }

    @Override
    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public User getOwner() {
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPunished(boolean punished) {
        this.punished = punished;
    }

    public boolean isPunished() {
        return punished;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
