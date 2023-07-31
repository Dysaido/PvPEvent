package xyz.dysaido.pvpevent.api.model;

public interface Model<I, T> {
    I getIdentifier();

    T getOwner();
}
