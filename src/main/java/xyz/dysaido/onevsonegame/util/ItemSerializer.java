package xyz.dysaido.onevsonegame.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemSerializer {

    public static String serialize(ItemStack[] itemStacks) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
            boos.writeInt(itemStacks.length);
            for (int i = 0; i < itemStacks.length; ++i) {
                boos.writeObject(itemStacks[i]);
            }
            boos.close();
            return Base64Coder.encodeLines(baos.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks!", e);
        }
    }

    public static ItemStack[] deserialize(String data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
            ItemStack[] itemStacks = new ItemStack[bois.readInt()];
            for (int i = 0; i < itemStacks.length; ++i) {
                itemStacks[i] = (ItemStack) bois.readObject();
            }
            bois.close();
            return itemStacks;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decode class type!", e);
        }
    }

}
