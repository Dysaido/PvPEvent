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

package xyz.dysaido.pvpevent.api;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.api.model.Match;
import xyz.dysaido.pvpevent.command.ParentCommand;
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;
import xyz.dysaido.pvpevent.model.manager.AutosetManager;
import xyz.dysaido.pvpevent.model.manager.KitManager;
import xyz.dysaido.pvpevent.model.manager.UserManager;
import xyz.dysaido.pvpevent.util.YamlStorage;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PvPEvent {

    void enable();

    boolean isActiveMatch();

    void disable();

    void reload();

    void reloadConfig();

    void setMainMatch(Match<UUID> mainMatch);

    Optional<Match<UUID>> getMainMatch();

    boolean hasMainMatch();

    void reloadStorages();

    JavaPlugin getPlugin();

    File getConfigFile();

    ParentCommand getCommandManager();

    ArenaManager getArenaManager();

    KitManager getKitManager();

    UserManager getUserManager();

    Map<String, User> getTop10Wins();

    AutosetManager getAutosetManager();
}
