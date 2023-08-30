package xyz.dysaido.pvpevent.util;

import org.bukkit.Bukkit;

public enum ServerVersion {
    NONE,
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_19_R1,
    v1_19_R2,
    v1_20_R1;

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