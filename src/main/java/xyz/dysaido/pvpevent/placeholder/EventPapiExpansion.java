package xyz.dysaido.pvpevent.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.dysaido.pvpevent.api.PvPEvent;

import java.util.UUID;

public class EventPapiExpansion extends PlaceholderExpansion {
    private final PvPEvent pvpEvent;
    public EventPapiExpansion(PvPEvent pvpEvent) {
        this.pvpEvent = pvpEvent;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        return internalRequest(player.getUniqueId(), identifier);
    }

    public String internalRequest(UUID id, String identifier) {
        return "";
    }

    @Override
    public @NotNull String getIdentifier() {
        return pvpEvent.getPlugin().getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return pvpEvent.getPlugin().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return pvpEvent.getPlugin().getDescription().getVersion();
    }
}