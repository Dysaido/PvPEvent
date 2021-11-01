package xyz.dysaido.onevsonegame.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class InventorySerializer {

    public static String serialize(Location location) {
        return location.getWorld().getName() + ","
                + location.getBlockX() + ","
                + location.getBlockY() + ","
                + location.getBlockZ() + ","
                + location.getYaw() + ","
                + location.getPitch();
    }

    public static Location deserialize(String serializedLocation) {

    }

    public static void serializeToConfig(Location location, ConfigurationSection configuration) {
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(configuration, "Section cannot be null");
        World w = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        configuration.set("world", w.getName());
        configuration.set("x", x);
        configuration.set("y", y);
        configuration.set("z", z);
        configuration.set("yaw", location.getYaw());
        configuration.set("pitch", location.getPitch());
    }

    public static Location deserializeFromConfig(ConfigurationSection configuration) {
        Objects.requireNonNull(configuration, "Section cannot be null");
        World w = Bukkit.getServer().getWorld(configuration.getString("world"));
        double x = configuration.getDouble("x");
        double y = configuration.getDouble("y");
        double z = configuration.getDouble("z");
        double yaw = configuration.getDouble("yaw");
        double pitch = configuration.getDouble("pitch");
        return new Location(w, x, y, z, (float) yaw, (float) pitch);
    }
}
