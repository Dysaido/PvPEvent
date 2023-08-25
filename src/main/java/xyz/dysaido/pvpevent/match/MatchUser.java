package xyz.dysaido.pvpevent.match;

import java.util.UUID;

public abstract class MatchUser {

    protected final UUID id;
    protected final AbstractMatch match;

    public MatchUser(UUID id, AbstractMatch match) {
        this.id = id;
        this.match = match;
    }

    public UUID getId() {
        return id;
    }

    public AbstractMatch getMatch() {
        return match;
    }
}
