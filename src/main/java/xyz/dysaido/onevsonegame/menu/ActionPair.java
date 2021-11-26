package xyz.dysaido.onevsonegame.menu;

import java.util.Optional;
import java.util.function.Consumer;

public class ActionPair<Key, Value> {
    private Key key;
    private Consumer<Value> action;

    public ActionPair(Key key) {
        this.key = key;
    }

    public void setAction(Consumer<Value> action) {
        this.action = action;
    }

    public Optional<Consumer<Value>> getAction() {
        return Optional.ofNullable(action);
    }

    public void setMaterial(Key key) {
        this.key = key;
    }

    public Key getMaterial() {
        return key;
    }
}
