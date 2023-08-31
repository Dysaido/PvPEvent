package xyz.dysaido.pvpevent;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.util.Logger;

public final class PvPEventLoader extends JavaPlugin {

    private static final String TAG= "Loader";
    private PvPEventPlugin event;
    private Metrics metrics;

    @Override
    public void onEnable() {
        if (this.event != null) {
            Logger.debug(TAG, String.format("Call enable method from %s", PvPEventPlugin.class));
            metrics = new Metrics(this, 19693);

            // Optional: Add custom charts
            this.event.enable();
        } else {
            Logger.error(TAG, "PvPEvent was unloaded by unknown reason, please restart the server or use plugman!");
        }
    }

    @Override
    public void onDisable() {
        Logger.debug(TAG, String.format("Call disable method from %s", PvPEventPlugin.class));
        if (this.event != null) {
            this.event.disable();
            this.event = null;
        }
        if (this.metrics != null) {
            this.metrics.shutdown();
        }

    }

    @Override
    public void onLoad() {
        Logger.debug(TAG, "Initialize...");
        this.event = new PvPEventPlugin(this);
    }

    public PvPEvent getPvPEvent() {
        return event;
    }
}
