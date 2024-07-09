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
