package xyz.dysaido.pvpevent.api.inventory;

import org.bukkit.Material;
import xyz.dysaido.pvpevent.util.ServerVersion;

import java.util.Arrays;
import java.util.Objects;

public class Materials {

    public static final Material SIGN = findByName("SIGN");
    public static final Material DIAMOND_HOE = findByName("DIAMOND_HOE");

    public static final Material REDSTONE = findByName("REDSTONE");
    public static final Material ANVIL = findByName("ANVIL");
    public static final Material DIAMOND_SWORD = findByName("DIAMOND_SWORD");
    public static final Material BOOK = findByName("BOOK");


    public static Material findByName(String... arrstring) {
        return Arrays.stream(arrstring).map(Materials::get).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static Material get(String name) {
        Material material;
        //TODO: FIX
        /*if (PvPEvent.getInstance().getServerVersion().afterEquals(ServerVersion.v1_13_R1) && (material = Material.getMaterial("LEGACY_" + name)) != null) {
            return material;
        }*/
        return Material.getMaterial(name);
    }

}
