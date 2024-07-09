/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        if (configuration == null) {
            configuration = createDataFile(file);
            reload(file.getName());
        }
        return configuration;
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
        final InputStream defConfigStream = getResource(file.getName());
        if (defConfigStream == null) return;
        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));

    }

    public void saveFile() {
        try {
            getFile().save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
        }
    }

}
