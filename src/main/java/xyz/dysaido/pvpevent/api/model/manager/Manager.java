package xyz.dysaido.pvpevent.api.model.manager;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Manager<I, T> extends Function<I, T> {

    T getIfPresent(I id);

    T getOrMake(I id);

    void load();

    void unload();

    boolean isLoaded(I id);

    T remove(I id);

    Map<I, T> getAll();

    default Action<T, Manager<I, T>> withAction(I i){
        T t = getOrMake(i);
        return new Action<>(t, this);
    }

    @FunctionalInterface
    interface Operation<T> {
        void perform(T user);
    }

    class Action<T, M> {
        private final T value;
        private final M manager;

        private Action(T value, M manager) {
            this.value = value;
            this.manager = manager;
        }

        public Action<T, M> then(Operation<T> operation) {
            operation.perform(value);
            return this;
        }

        public <R> Action<R, M> mapM(Function<? super M, ? super R> mapper) {
            R mapped = (R) mapper.apply(manager);
            return new Action<>(mapped, manager);
        }

        public <R> Action<R, M> mapT(Function<? super T, ? super R> mapper) {
            R mapped = (R) mapper.apply(value);
            return new Action<>(mapped, manager);
        }

        public Action<T, M> then(BiConsumer<T, M> operation) {
            operation.accept(value, manager);
            return this;
        }

        public T get() {
            return value;
        }
    }
}
