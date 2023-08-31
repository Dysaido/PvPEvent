package xyz.dysaido.pvpevent.model;

import java.util.Date;

public class AutoRun {

    private final String name;
    private String arenaName;
    private String command;
    private String broadcast;
    private Date date;
    public AutoRun(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setDate(long date) {
        this.date = new Date(date);
    }

    public Date getDate() {
        return date;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }
}
