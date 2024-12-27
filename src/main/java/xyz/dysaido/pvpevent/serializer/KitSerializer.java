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

package xyz.dysaido.pvpevent.serializer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.dysaido.pvpevent.match.Kit;
import xyz.dysaido.pvpevent.model.manager.KitManager;
import xyz.dysaido.pvpevent.util.Logger;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

public class KitSerializer {
    private static final String TAG = "KitSerializer";
    private final KitManager kitManager;
    private final YamlStorage storage;
    public KitSerializer(KitManager kitManager, YamlStorage storage) {
        this.kitManager = kitManager;
        this.storage = storage;
    }

    public List<Kit<Player>> read() {
        List<Kit<Player>> kits = new ArrayList<>();
        FileConfiguration conf = storage.getFile();
        if (conf.isConfigurationSection("kits")) {
            ConfigurationSection kitsSection = conf.getConfigurationSection("kits");
            for (String sectionName : kitsSection.getKeys(false)) {
                ConfigurationSection section = kitsSection.getConfigurationSection(sectionName);
                try {
                    ItemStack[] contents = section.getList("contents").toArray(new ItemStack[0]);
                    ItemStack[] armor = section.getList("armor").toArray(new ItemStack[0]);
                    Kit<Player> kit = new Kit<>(sectionName);
                    kit.setArmor(armor);
                    kit.setContents(contents);
                    kits.add(kit);
                } catch (Throwable e) {
                    try {
                        kits.add(new Kit<>(sectionName));
                    } catch (Throwable e2) {
                        e.addSuppressed(e2);
                        Bukkit.getLogger().log(Level.WARNING, "Error:", e);
                        Logger.warn(TAG, String.format("Cannot read %s from kits.yml. Suppose to delete this section from yaml file!", sectionName));
                    }
                }
            }
        }
        return kits;
    }

    public void append(Kit<Player> kit) {
        ConfigurationSection kitsSection = storage.getFile().getConfigurationSection("kits");
        if (kitsSection == null) {
            kitsSection = storage.getFile().createSection("kits");
        }

        writeKit(kitsSection, kit);

        storage.saveFile();
    }

    public void remove(String kitName) {
        ConfigurationSection kitsSection = storage.getFile().getConfigurationSection("kits");
        if (kitsSection != null) {
            if (kitsSection.isConfigurationSection(kitName)) {
                kitsSection.set(kitName, null);
                this.storage.saveFile();
            }
        }
    }

    public void write() {
        ConfigurationSection kitsSection = storage.getFile().getConfigurationSection("kits");
        if (kitsSection == null) {
            kitsSection = storage.getFile().createSection("kits");
        }

        final ConfigurationSection finalKitsSection = kitsSection;
        stream().forEach(kit -> writeKit(finalKitsSection, kit));

        this.storage.saveFile();
    }

    private void writeKit(ConfigurationSection kitsSection, Kit<Player> kit) {
        ConfigurationSection section = kitsSection.createSection(kit.getName());
        section.set("contents", kit.getContents());
        section.set("armor", kit.getArmor());
    }

    protected Stream<Kit<Player>> stream() {
        return kitManager.getAll().values().stream().sorted(Comparator.comparing(Kit::getName));
    }

    public YamlStorage getStorage() {
        return storage;
    }
}
