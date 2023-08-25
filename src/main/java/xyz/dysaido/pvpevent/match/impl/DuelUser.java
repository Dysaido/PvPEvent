package xyz.dysaido.pvpevent.match.impl;

import xyz.dysaido.pvpevent.match.AbstractMatch;
import xyz.dysaido.pvpevent.match.MatchUser;

import java.util.UUID;

public class DuelUser extends MatchUser {

    private State state = State.QUEUE;
    private final Object fightCountdown = null;
    private final DuelUser target = null;
    public DuelUser(UUID id, AbstractMatch match) {
        super(id, match);
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public enum State {
        QUEUE, FIGHTING, SPECTATE
    }
}
