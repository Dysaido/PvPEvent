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

package xyz.dysaido.pvpevent.api.pagination;

import org.bukkit.Material;

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
        if ((material = Material.getMaterial("LEGACY_" + name)) != null) {
            return material;
        }
        return Material.getMaterial(name);
    }

}
