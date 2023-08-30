package xyz.dysaido.pvpevent.api.pagination;

import org.bukkit.Material;
import xyz.dysaido.pvpevent.util.ServerVersion;

import java.util.Arrays;
import java.util.Objects;

public enum Materials {

    SIGN("SIGN"),
    DIAMOND_HOE("DIAMOND_HOE"),
    REDSTONE("REDSTONE"),
    CLOCK("WATCH", "CLOCK"),
    ANVIL("ANVIL"),
    DIAMOND_SWORD("DIAMOND_SWORD"),
    BOOK("BOOK");

    private final Material material;
    Materials(String... array) {
        Material material;
        if (array.length == 0) {
            material = findByName(name());
        } else {
            material = findByName(array);
        }
        this.material = material;
    }

    public Material asBukkit() {
        return material;
    }

    public static Material findByName(String... arrstring) {
        return Arrays.stream(arrstring).map(Materials::get).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static Material get(String name) {
        Material material;
        if (ServerVersion.runtimeVersion().afterEquals(ServerVersion.v1_13_R1) && (material = Material.getMaterial("LEGACY_" + name)) != null) {
            return material;
        }
        return Material.getMaterial(name);
    }

}
