package xyz.dysaido.pvpevent.model.manager;

import xyz.dysaido.pvpevent.api.model.manager.Manager;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractManager<I, T> implements Manager<I, T> {

    @Override
    public T getIfPresent(I id) {
        return objects().get(id);
    }

    @Override
    public T getOrMake(I id) {
        T t = objects().get(id);
        if (t != null) {
            return t;
        }
        return objects().computeIfAbsent(id, this);
    }

    @Override
    public void unload() {
        objects().clear();
    }

    @Override
    public T remove(I id) {
        T t = getIfPresent(id);
        objects().remove(id);
        return t;
    }

    @Override
    public boolean isLoaded(I id) {
        return objects().containsKey(id);
    }

    @Override
    public Map<I, T> getAll() {
        return Collections.unmodifiableMap(objects());
    }

    protected abstract Map<I, T> objects();
}
