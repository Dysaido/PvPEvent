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
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.serializer.UserSerializer;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserManager extends AbstractManager<UUID, User> {

    private final Map<UUID, User> objects = new ConcurrentHashMap<>();
    private final PvPEventPlugin pvpEvent;
    private final UserSerializer serializer;

    public UserManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new UserSerializer(this, YamlStorage.of(pvpEvent.getPlugin(), "users"));
    }

    public User getOrMake(UUID id, String name) {
        return getOrMake(id, name, false);
    }

    public User getOrMake(UUID id, String name, boolean save) {
        User user = getOrMake(id);
        if (name != null) {
            user.setName(name);
        }
        if (save) {
            serializer.append(user);
        }
        return user;
    }

    @Override
    public User remove(UUID id) {
        serializer.remove(id);
        return super.remove(id);
    }

    @Override
    public void load() {
        CompletableFuture.supplyAsync(serializer::read)
                .whenComplete((users, throwable) -> {
                    Map<UUID, User> usersByUuid = users.stream()
                            .collect(Collectors.toMap(User::getIdentifier, Function.identity()));
                    this.objects.putAll(usersByUuid);
                });
    }

    public List<User> getToplist(int limit) {
        List<User> topList = new ArrayList<>(objects.values());
        topList.sort(Comparator.comparingInt(User::getWins).reversed());

        List<User> resultList = new ArrayList<>();

        Iterator<User> iterator = topList.iterator();

        for (int i = 0; i < Math.min(10, limit) && iterator.hasNext(); i++) {
            resultList.add(iterator.next());
        }

        return Collections.unmodifiableList(resultList);
    }

    @Override
    public void unload() {
        serializer.write();
        super.unload();
    }

    @Override
    public User apply(UUID uuid) {
        User ret =  new User(uuid);
        serializer.append(ret);

        return ret;
    }

    public UserSerializer getSerializer() {
        return serializer;
    }

    @Override
    protected Map<UUID, User> objects() {
        return objects;
    }
}
