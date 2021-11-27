package xyz.dysaido.onevsonegame.menu;

import java.util.Optional;
import java.util.function.Consumer;

public class ActionPair<Key, In> {
    private Key key;
    private Consumer<In> action;

    public ActionPair(Key key) {
        this.key = key;
    }

    public void setAction(Consumer<In> action) {
        this.action = action;
    }

    public Optional<Consumer<In>> getAction() {
        return Optional.ofNullable(action);
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }
}
