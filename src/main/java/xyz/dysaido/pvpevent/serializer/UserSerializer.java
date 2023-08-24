package xyz.dysaido.pvpevent.serializer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.model.manager.UserManager;
import xyz.dysaido.pvpevent.util.Logger;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class UserSerializer {
    private static final String TAG = "UserSerializer";
    private final UserManager userManager;
    private final YamlStorage storage;
    public UserSerializer(UserManager userManager, YamlStorage storage) {
        this.userManager = userManager;
        this.storage = storage;
    }

    public List<User> read() {
        List<User> users = new ArrayList<>();
        FileConfiguration conf = storage.getFile();
        for (String sectionName : conf.getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection(sectionName);
            try {
                User user = new User(UUID.fromString(sectionName));
                user.setName(Optional.ofNullable(section.getString("name")).orElse("undefined"));
                user.setKills(section.getInt("kills"));
                user.setDeaths(section.getInt("deaths"));
                user.setWins(section.getInt("wins"));
                user.setPunished(section.getBoolean("punished"));
                users.add(user);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Error:", e);
                Logger.warning(TAG, String.format("Cannot read %s from users.yml. Suppose to delete this section from yaml file!", sectionName));
            }
        }
        return users;
    }

    public void append(User user) {
        FileConfiguration conf = storage.getFile();
        writeUser(conf, user);

        storage.saveFile();
    }

    public void remove(UUID identifier) {
        FileConfiguration conf = storage.getFile();
        if (conf.isConfigurationSection(identifier.toString())) {
            conf.set(identifier.toString(), null);
            this.storage.saveFile();
        }
    }

    public void write() {
        FileConfiguration conf = storage.getFile();
        stream().forEach(user -> writeUser(conf, user));

        this.storage.saveFile();
    }

    private void writeUser(FileConfiguration conf, User user) {
        ConfigurationSection section = storage.getFile().getConfigurationSection(user.getIdentifier().toString());
        if (section == null) {
            section = conf.createSection(user.getIdentifier().toString());
        }

        section.set("name", user.getName());
        section.set("kills", user.getKills());
        section.set("deaths", user.getDeaths());
        section.set("wins", user.getWins());
        section.set("punished", user.isPunished());
    }

    protected Stream<User> stream() {
        return userManager.getAll().values().stream().sorted(Comparator.comparing(User::getName));
    }

    public YamlStorage getStorage() {
        return storage;
    }
}
