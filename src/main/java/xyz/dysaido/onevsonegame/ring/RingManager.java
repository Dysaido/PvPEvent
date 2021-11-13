package xyz.dysaido.onevsonegame.ring;

import xyz.dysaido.onevsonegame.util.FileManager;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RingManager {
    private final Map<String, Ring> arenaMap = new HashMap<>();
    private final static String TAG = "RingManager";
    private final FileManager ringConfig;
    public RingManager(FileManager fileManager) {
        this.ringConfig = fileManager;
        // TODO: load from config some arenas
    }

    public Ring get(String name) {
        return arenaMap.get(name);
    }

    public Ring add(Ring ring) {
        return arenaMap.computeIfPresent(ring.getName(),(s, internalRing) -> {
            Logger.warning(TAG, String.format("You can't make an ring that already exists (%s)", s));
            return internalRing;
        });
//        return arenaMap.putIfAbsent(ring.getName(), ring);
    }

    public Ring remove(Ring ring) {
        return remove(ring.getName());
    }

    public Ring remove(String name) {
        return arenaMap.remove(name);
    }

    public Collection<Ring> getRings() {
        return arenaMap.values();
    }

}
