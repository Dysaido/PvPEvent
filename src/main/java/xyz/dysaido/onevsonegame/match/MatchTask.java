package xyz.dysaido.onevsonegame.match;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.dysaido.onevsonegame.OneVSOneGame;

public abstract class MatchTask {

    private final String name;
    protected final OneVSOneGame plugin;
    private BukkitTask task;

    public MatchTask(String name, OneVSOneGame plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::run, 0, 1);
    }

    public void stop() {
        task.cancel();
    }

    public String name() {
        return name;
    }

    public abstract void run();

}
