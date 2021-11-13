package xyz.dysaido.onevsonegame.kit;

import xyz.dysaido.onevsonegame.util.FileManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitManager {

    private final Map<String, Kit> kitMap = new ConcurrentHashMap<>();
    private final FileManager kitConfig;

    public KitManager(FileManager fileManager) {
        this.kitConfig = fileManager;
    }

    private void load() {

    }

    public void add() {

    }

    public void remove() {

    }

    public void save() {

    }
}
