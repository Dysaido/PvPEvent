package xyz.dysaido.pvpevent.serializer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.dysaido.pvpevent.model.AutoRun;
import xyz.dysaido.pvpevent.model.manager.AutosetManager;
import xyz.dysaido.pvpevent.util.Logger;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class AutosetSerializer {
    private static final String TAG = "AutosetSerializer";
    private final AutosetManager autosetManager;
    private final YamlStorage storage;
    public AutosetSerializer(AutosetManager autosetManager, YamlStorage storage) {
        this.autosetManager = autosetManager;
        this.storage = storage;
    }

    public List<AutoRun> read() {
        List<AutoRun> runs = new ArrayList<>();
        FileConfiguration conf = storage.getFile();
        for (String sectionName : conf.getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection(sectionName);
            try {
                AutoRun run = new AutoRun(sectionName);
                run.setArenaName(section.getString("arena"));
                run.setBroadcast(section.getString("broadcast"));
                run.setCommand(section.getString("command"));
                run.setDate(section.getLong("date"));
                runs.add(run);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Error:", e);
                Logger.warning(TAG, String.format("Cannot read %s from autosets.yml. Suppose to delete this section from yaml file!", sectionName));
            }
        }
        return runs;
    }

    public void append(AutoRun run) {
        FileConfiguration conf = storage.getFile();
        writePojo(conf, run);

        storage.saveFile();
    }

    public void remove(String identifier) {
        FileConfiguration conf = storage.getFile();
        if (conf.isConfigurationSection(identifier)) {
            conf.set(identifier, null);
            this.storage.saveFile();
        }
    }

    public void write() {
        FileConfiguration conf = storage.getFile();
        stream().forEach(run -> writePojo(conf, run));

        this.storage.saveFile();
    }

    private void writePojo(FileConfiguration conf, AutoRun run) {
        ConfigurationSection section = storage.getFile().getConfigurationSection(run.getName());
        if (section == null) {
            section = conf.createSection(run.getName());
        }

        section.set("arena", run.getName());
        section.set("broadcast", run.getBroadcast());
        section.set("command", run.getCommand());
        section.set("date", run.getDate().toString());
    }

    protected Stream<AutoRun> stream() {
        return autosetManager.getAll().values().stream().sorted(Comparator.comparing(AutoRun::getName));
    }

    public YamlStorage getStorage() {
        return storage;
    }
}
