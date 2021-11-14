package xyz.dysaido.onevsonegame.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class LocationSerializer {

    public static String serialize(Location location) {
        return location.getWorld().getName() + ","
                + location.getBlockX() + ","
                + location.getBlockY() + ","
                + location.getBlockZ() + ","
                + location.getYaw() + ","
                + location.getPitch();
    }

    public static Location deserialize(String serializedLocation) {
        String[] strings = serializedLocation.split(",");
        World world = Bukkit.getServer().getWorld(strings[0]);
        double blockX = Double.parseDouble(strings[1]);
        double blockY = Double.parseDouble(strings[2]);
        double blockZ = Double.parseDouble(strings[3]);
        float yaw = Float.parseFloat(strings[4]);
        float pitch = Float.parseFloat(strings[5]);
        return new Location(world, blockX, blockY, blockZ, yaw, pitch);
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
