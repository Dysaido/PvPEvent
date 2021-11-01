package xyz.dysaido.onevsonegame.util;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class FileManager {
    private final static String TAG = "FileManager";
    private final JavaPlugin javaPlugin;
    private final File mFile;
    private FileConfiguration mFileConfiguration;

    public FileManager(JavaPlugin plugin, String name) {
        this.javaPlugin = Objects.requireNonNull(plugin);
        this.mFile = createFile(Objects.requireNonNull(name));
    }

    public FileManager(JavaPlugin plugin, File file) {
        this.javaPlugin = Objects.requireNonNull(plugin);
        this.mFile = Objects.requireNonNull(file);
    }

    private FileConfiguration createDataFile(File file) {
        Logger.information(TAG, "CreateDataFile");
        FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                ymlFormat.save(file);
            } catch (IOException e) {
                Logger.error(TAG, e.getMessage());
            }
            return ymlFormat;
        }
        return ymlFormat;
    }

    private File createFile(String name) {
        return new File(javaPlugin.getDataFolder(), name + ".yml");
    }

    private void reloadFile(String filename) {
        Logger.information(TAG, "ReloadFile");
        final InputStream defConfigStream = getResource(filename);
        if (defConfigStream == null) return;
        mFileConfiguration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    private InputStream getResource(String filename) {
        if (filename == null) throw new IllegalArgumentException("Filename cannot be null");
        try {
            URL url = javaPlugin.getClass().getClassLoader().getResource(filename);
            if (url == null) return null;
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            Logger.error(TAG, e.getMessage());
            return null;
        }
    }

    public FileConfiguration getFile() {
        if (mFileConfiguration == null) {
            mFileConfiguration = Objects.requireNonNull(createDataFile(mFile));
            reloadFile(mFile.getName());
        }
        return mFileConfiguration;
    }

    public void reloadFile() {
        Logger.information(TAG, "ReloadFile");
        mFileConfiguration = YamlConfiguration.loadConfiguration(mFile);
        final InputStream defConfigStream = getResource(mFile.getName());
        if (defConfigStream == null) return;
        mFileConfiguration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public void saveFile() {
        Logger.information(TAG, "SaveFile");
        try {
            getFile().save(mFile);
        } catch (IOException e) {
            Logger.error(TAG, e.getMessage());
        }
    }
}
