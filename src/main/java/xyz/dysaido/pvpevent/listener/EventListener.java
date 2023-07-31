package xyz.dysaido.pvpevent.listener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.dysaido.pvpevent.api.PvPEvent;

public abstract class EventListener implements Listener {

    protected final PvPEvent pvpEvent;
    public EventListener(PvPEvent pvpEvent) {
        this.pvpEvent = pvpEvent;
    }

    public void load() {
        pvpEvent.getPlugin().getServer().getPluginManager().registerEvents(this, pvpEvent.getPlugin());
    }

    public void unload() {
        HandlerList.unregisterAll(this);
    }

}
