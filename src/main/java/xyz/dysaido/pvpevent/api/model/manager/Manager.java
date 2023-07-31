package xyz.dysaido.pvpevent.api.model.manager;

import java.util.Map;
import java.util.function.Function;

public interface Manager<I, T> extends Function<I, T> {

    T getIfPresent(I id);

    T getOrMake(I id);

    void load();

    void unload();

    boolean isLoaded(I id);

    T remove(I id);

    Map<I, T> getAll();
}
