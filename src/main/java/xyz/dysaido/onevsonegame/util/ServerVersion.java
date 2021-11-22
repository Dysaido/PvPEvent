package xyz.dysaido.onevsonegame.util;

public enum ServerVersion {
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1;

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
}