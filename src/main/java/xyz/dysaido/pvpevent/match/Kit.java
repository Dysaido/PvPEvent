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

package xyz.dysaido.pvpevent.match;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Kit<T extends Player> implements Consumer<T> {

    public static final Kit<Player> EMPTY = new Kit<>("NULL");

    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;

    public Kit(String name) {
        this(name, null, null);
    }

    public Kit(String name, ItemStack[] contents, ItemStack[] armor) {
        this.name = name;
        this.contents = contents;
        this.armor = armor;
    }

    @Override
    public void accept(T player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");
        if (contents != null) {
            player.getInventory().setContents(contents);
        }
        player.getInventory().setArmorContents(armor);
        player.updateInventory();
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

}
