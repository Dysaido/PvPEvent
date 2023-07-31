package xyz.dysaido.pvpevent.model.manager;

import org.bukkit.entity.Player;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.match.Kit;
import xyz.dysaido.pvpevent.serializer.KitSerializer;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KitManager extends AbstractManager<String, Kit<Player>> {

    private final PvPEventPlugin pvpEvent;
    private final KitSerializer serializer;

    public KitManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new KitSerializer(this, YamlStorage.of(pvpEvent.getPlugin(), "kits"));
    }

    @Override
    public Kit<Player> getOrMake(String id) {
        Kit<Player> kit =  super.getOrMake(id);
        serializer.write();
        return kit;
    }

    @Override
    public Kit<Player> remove(String id) {
        Kit<Player> kit = getIfPresent(id);
        objects.remove(id);
        serializer.write();
        return kit;
    }

    @Override
    public void load() {
        List<Kit<Player>> kits = serializer.read();
        Map<String, Kit<Player>> kitsByName = kits.stream().collect(Collectors.toMap(Kit::getName, Function.identity()));
        this.objects.putAll(kitsByName);
    }

    @Override
    public void unload() {
        serializer.write();
        this.objects.clear();
    }

    @Override
    public Kit<Player> apply(String s) {
        return new Kit<>(s);
    }

    public KitSerializer getSerializer() {
        return serializer;
    }
}
