package xyz.dysaido.pvpevent.serializer;

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
                } catch (Exception e) {
                    Logger.warning(TAG, String.format("Cannot read %s from kits.yml. Suppose to delete this section from yaml file!", sectionName));
                    e.printStackTrace();
                }
            }
        }
        return kits;
    }

    public void write() {
        FileConfiguration conf = storage.getFile();
        if (!conf.isConfigurationSection("kits")) {
            conf.createSection("kits");
        }
        ConfigurationSection kitsSection = conf.getConfigurationSection("kits");
        stream().forEach(kit -> {
            ConfigurationSection section = kitsSection.createSection(kit.getName());
            section.set("contents", kit.getContents());
            section.set("armor", kit.getArmor());
        });
        this.storage.saveFile();
    }

    protected Stream<Kit<Player>> stream() {
        return kitManager.getAll().values().stream().sorted(Comparator.comparing(Kit::getName));
    }

    public YamlStorage getStorage() {
        return storage;
    }
}
