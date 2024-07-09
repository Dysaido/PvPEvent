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

import org.bukkit.entity.Player;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.match.Kit;
import xyz.dysaido.pvpevent.serializer.KitSerializer;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KitManager extends AbstractManager<String, Kit<Player>> {

    private final Map<String, Kit<Player>> objects = new HashMap<>();
    private final PvPEventPlugin pvpEvent;
    private final KitSerializer serializer;

    public KitManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new KitSerializer(this, YamlStorage.of(pvpEvent.getPlugin(), "kits"));
    }

    @Override
    public Kit<Player> getOrMake(String id) {
        Kit<Player> kit =  super.getOrMake(id);
        serializer.write();
        return kit;
    }

    @Override
    public Kit<Player> remove(String id) {
        Kit<Player> kit = getIfPresent(id);
        objects.remove(id);
        serializer.remove(id);
        return kit;
    }

    @Override
    public void load() {
        List<Kit<Player>> kits = serializer.read();
        Map<String, Kit<Player>> kitsByName = kits.stream().collect(Collectors.toMap(Kit::getName, Function.identity()));
        this.objects.putAll(kitsByName);
    }

    @Override
    public void unload() {
        serializer.write();
        this.objects.clear();
    }

    @Override
    public Kit<Player> apply(String s) {
        return new Kit<>(s);
    }

    public KitSerializer getSerializer() {
        return serializer;
    }

    @Override
    protected Map<String, Kit<Player>> objects() {
        return objects;
    }
}
