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

package xyz.dysaido.pvpevent.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.function.Supplier;

public class CustomLocation {
    private final String worldName;
    private float x; // -30_000_000 - 30_000_000
    private float y; // 0 - 4096
    private float z; // -30_000_000 - 30_000_000
    private int yaw; // ?
    private int pitch; // ?
    private Location bukkitLocation;

    public static CustomLocation of(JsonObject json) {
        String worldName = "";
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;
        int yaw = 0;
        int pitch = 0;
        JsonElement element = json.get("world");
        if (element != null) {
            worldName = element.getAsString();
        }
        element = json.get("x");
        if (element != null) {
            x = element.getAsFloat();
        }
        element = json.get("y");
        if (element != null) {
            y = element.getAsFloat();
        }
        element = json.get("z");
        if (element != null) {
            z = element.getAsFloat();
        }
        element = json.get("yaw");
        if (element != null) {
            yaw = element.getAsInt();
        }
        element = json.get("pitch");
        if (element != null) {
            pitch = element.getAsInt();
        }
        return new CustomLocation(worldName, x, y, z, yaw, pitch);
    }
    public static CustomLocation of(Location location) {
        String worldName = location.getWorld().getName();
        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();
        int yaw = (int) location.getYaw();
        int pitch = (int) location.getPitch();
        return new CustomLocation(worldName, x, y, z, yaw, pitch);
    }

    public CustomLocation(String worldName, float x, float y, float z) {
        this(worldName, x, y, z, 0, 0);
    }

    public CustomLocation(String worldName, float x, float y, float z, int yaw, int pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }


    public JsonElement serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("world", worldName);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("z", z);
        object.addProperty("yaw", yaw);
        object.addProperty("pitch", pitch);

        return object;
    }

    public Location asBukkit() {
        return new Location(Bukkit.getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }

    public Location asBukkit(boolean lazy) {
        if (lazy) {
            if (bukkitLocation == null) {
                bukkitLocation = asBukkit();
            }
            return bukkitLocation;
        }
        return asBukkit();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public int getYaw() {
        return yaw;
    }

    public int getPitch() {
        return pitch;
    }

    public String getWorldName() {
        return worldName;
    }
}
