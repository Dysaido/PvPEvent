package xyz.dysaido.pvpevent;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.util.Logger;

public final class PvPEventLoader extends JavaPlugin {

    private static final String TAG= "Loader";
    private PvPEventPlugin event;

    @Override
    public void onEnable() {
        if (this.event != null) {
            Logger.debug(TAG, String.format("Call enable method from %s", PvPEventPlugin.class));
            this.event.enable();
        } else {
            Logger.error(TAG, "PvPEvent was unloaded by unknown reason, please restart the server or use plugman!");
        }
    }

    @Override
    public void onDisable() {
        Logger.debug(TAG, String.format("Call disable method from %s", PvPEventPlugin.class));
        this.event.disable();
        this.event = null;
    }

    @Override
    public void onLoad() {
        Logger.debug(TAG, "Initialize...");
        this.event = new PvPEventPlugin(this);
    }

    public PvPEventPlugin getPvPEvent() {
        return event;
    }
}
