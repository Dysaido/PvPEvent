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

import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.model.AutoRun;
import xyz.dysaido.pvpevent.serializer.AutosetSerializer;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutosetManager extends AbstractManager<String, AutoRun> {

    private final Map<String, AutoRun> runsById = new ConcurrentHashMap<>();
    private final PvPEventPlugin pvpEvent;
    private final AutosetSerializer serializer;
    public AutosetManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new AutosetSerializer(this, YamlStorage.of(pvpEvent.getPlugin(), "autosets"));
    }

    @Override
    public AutoRun remove(String id) {
        serializer.remove(id);
        return super.remove(id);
    }

    @Override
    public void load() {
        Map<String, AutoRun> runsById = serializer.read().stream()
                .collect(Collectors.toMap(AutoRun::getName, Function.identity()));
        this.runsById.putAll(runsById);
    }

    @Override
    public void unload() {
        serializer.write();
        super.unload();
    }

    @Override
    protected Map<String, AutoRun> objects() {
        return runsById;
    }

    @Override
    public AutoRun apply(String s) {
        AutoRun run = new AutoRun(s);
        serializer.append(run);

        return run;
    }

    public AutosetSerializer getSerializer() {
        return serializer;
    }
}
