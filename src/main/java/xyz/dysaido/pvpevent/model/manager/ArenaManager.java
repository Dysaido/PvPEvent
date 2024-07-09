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
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.serializer.ArenaSerializer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArenaManager extends AbstractManager<String, Arena> {

    private final Map<String, Arena> objects = new HashMap<>();
    private final PvPEventPlugin pvpEvent;
    private final ArenaSerializer serializer;

    public ArenaManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new ArenaSerializer(this, new File(pvpEvent.getPlugin().getDataFolder(), "arenas.json"));
    }

    @Override
    public Arena getOrMake(String id) {
        Arena arena = super.getOrMake(id);
        serializer.write();
        return arena;
    }

    @Override
    public Arena remove(String id) {
        Arena arena = getIfPresent(id);
        objects.remove(id);
        serializer.write();
        return arena;
    }

    @Override
    public void load() {
        List<Arena> arenas = serializer.read();
        Map<String, Arena> arenasByName = arenas.stream().collect(Collectors.toMap(Arena::getIdentifier, Function.identity()));
        this.objects.putAll(arenasByName);
    }

    @Override
    public Arena apply(String s) {
        return new Arena(s);
    }

    public ArenaSerializer getSerializer() {
        return serializer;
    }

    @Override
    protected Map<String, Arena> objects() {
        return objects;
    }
}
