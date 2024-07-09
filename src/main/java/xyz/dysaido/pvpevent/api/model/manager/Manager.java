/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        void perform(T t);
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
