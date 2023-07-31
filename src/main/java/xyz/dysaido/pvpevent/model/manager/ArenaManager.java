package xyz.dysaido.pvpevent.model.manager;

import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.serializer.ArenaSerializer;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArenaManager extends AbstractManager<String, Arena> {
    private final PvPEventPlugin pvpEvent;
    private final ArenaSerializer serializer;

    public ArenaManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new ArenaSerializer(this, new File(pvpEvent.getPlugin().getDataFolder(), "arenas.json"));
    }

    @Override
    public Arena getOrMake(String id) {
        Arena arena = super.getOrMake(id);
        serializer.write();
        return arena;
    }

    @Override
    public Arena remove(String id) {
        Arena arena = getIfPresent(id);
        objects.remove(id);
        serializer.write();
        return arena;
    }

    @Override
    public void load() {
        List<Arena> arenas = serializer.read();
        Map<String, Arena> arenasByName = arenas.stream().collect(Collectors.toMap(Arena::getIdentifier, Function.identity()));
        this.objects.putAll(arenasByName);
    }

    @Override
    public Arena apply(String s) {
        return new Arena(s);
    }

    public ArenaSerializer getSerializer() {
        return serializer;
    }
}
