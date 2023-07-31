package xyz.dysaido.pvpevent.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;

public class YamlStorage {

    private final JavaPlugin plugin;
    private final File file;
    private volatile FileConfiguration configuration;

    public static YamlStorage of(JavaPlugin plugin, String name) {
        return new YamlStorage(plugin, name);
    }

    private YamlStorage(JavaPlugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name + ".yml"));
    }

    private YamlStorage(JavaPlugin plugin, File file) {
        this.plugin = Objects.requireNonNull(plugin);
        this.file = Objects.requireNonNull(file);
    }

    private FileConfiguration createDataFile(File file) {
        Objects.requireNonNull(file);
        FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                ymlFormat.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            }
            return ymlFormat;
        }
        return ymlFormat;
    }

    private void reload(String filename) {
        Objects.requireNonNull(filename);
        final InputStream defConfigStream = getResource(filename);
        if (defConfigStream == null) return;
        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
    }

    private InputStream getResource(String filename) {
        Objects.requireNonNull(filename);
        try {
            URL url = plugin.getClass().getClassLoader().getResource(filename);
            if (url == null) return null;
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    public FileConfiguration getFile() {
        synchronized (this) {
            if (configuration == null) {
                configuration = createDataFile(file);
                reload(file.getName());
            }
            return configuration;
        }
    }

    public void reload() {
        synchronized (this) {
            configuration = YamlConfiguration.loadConfiguration(file);
            final InputStream defConfigStream = getResource(file.getName());
            if (defConfigStream == null) return;
            configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        }
    }

    public void saveFile() {
        synchronized (this) {
            try {
                getFile().save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            }
        }
    }

}
