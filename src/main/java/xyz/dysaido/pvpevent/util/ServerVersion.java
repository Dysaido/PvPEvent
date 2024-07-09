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

import org.bukkit.Bukkit;

public enum ServerVersion {
    NONE,
    v1_8_R3,
    v1_16_R3,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3;

    public boolean before(ServerVersion serverVersion) {
        return serverVersion.ordinal() > this.ordinal();
    }

    public boolean afterEquals(ServerVersion serverVersion) {
        return this.ordinal() >= serverVersion.ordinal();
    }

    public boolean beforeEquals(ServerVersion serverVersion) {
        return serverVersion.ordinal() >= this.ordinal();
    }

    public boolean after(ServerVersion serverVersion) {
        return this.ordinal() > serverVersion.ordinal();
    }

    private static final String RUNTIME_VERSION_STRING;
    private static final ServerVersion RUNTIME_VERSION;

    static {
        String serverVersion = "";
        // check we're dealing with a "CraftServer" and that the server isn't non-versioned.
        Class<?> server = Bukkit.getServer().getClass();
        if (server.getSimpleName().equals("CraftServer") && !server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            String obcPackage = server.getPackage().getName();
            // check we're dealing with a craftbukkit implementation.
            if (obcPackage.startsWith("org.bukkit.craftbukkit.")) {
                // return the package version.
                serverVersion = obcPackage.substring("org.bukkit.craftbukkit.".length());
            }
        }
        RUNTIME_VERSION_STRING = serverVersion;

        ServerVersion runtimeVersion = null;
        if (RUNTIME_VERSION_STRING.isEmpty()) {
            runtimeVersion = ServerVersion.NONE;
        } else {
            try {
                runtimeVersion = ServerVersion.valueOf(serverVersion);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        RUNTIME_VERSION = runtimeVersion;
    }

    public static ServerVersion runtimeVersion() {
        if (RUNTIME_VERSION == null) {
            throw new IllegalStateException("Unknown package version: " + RUNTIME_VERSION_STRING);
        }
        return RUNTIME_VERSION;
    }
}