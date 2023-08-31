package xyz.dysaido.pvpevent.model.manager;

import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.model.AutoRun;
import xyz.dysaido.pvpevent.serializer.AutosetSerializer;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutosetManager extends AbstractManager<String, AutoRun> {

    private final Map<String, AutoRun> runsById = new ConcurrentHashMap<>();
    private final PvPEventPlugin pvpEvent;
    private final AutosetSerializer serializer;
    public AutosetManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new AutosetSerializer(this, YamlStorage.of(pvpEvent.getPlugin(), "autosets"));
    }

    @Override
    public AutoRun remove(String id) {
        serializer.remove(id);
        return super.remove(id);
    }

    @Override
    public void load() {
        Map<String, AutoRun> runsById = serializer.read().stream()
                .collect(Collectors.toMap(AutoRun::getName, Function.identity()));
        this.runsById.putAll(runsById);
    }

    @Override
    public void unload() {
        serializer.write();
        super.unload();
    }

    @Override
    protected Map<String, AutoRun> objects() {
        return runsById;
    }

    @Override
    public AutoRun apply(String s) {
        AutoRun run = new AutoRun(s);
        serializer.append(run);

        return run;
    }

    public AutosetSerializer getSerializer() {
        return serializer;
    }
}
