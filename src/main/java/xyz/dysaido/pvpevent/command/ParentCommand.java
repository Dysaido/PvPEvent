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

package xyz.dysaido.pvpevent.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.util.CommandMapUtil;
import xyz.dysaido.pvpevent.util.Logger;

import java.util.Map;

public class ParentCommand extends AbstractCommand {
    private static final String TAG = "ParentCommand";
    public ParentCommand(PvPEventPlugin pvpEvent, String name) {
        super(pvpEvent, name);
    }

    public void load() {
        Logger.debug(TAG, String.format("Load - name: %s, perm: %s", getName(), getPermission()));
        CommandMapUtil.getCommandMap(Bukkit.getServer()).register("pvpevent", this);
    }

    public void unload() {
        Logger.debug(TAG, String.format("Unload - name: %s, perm: %s", getName(), getPermission()));
        SimpleCommandMap commandMap = CommandMapUtil.getCommandMap(Bukkit.getServer());
        Map<String, Command> knownCommands = CommandMapUtil.getKnownCommands(commandMap);
        knownCommands.remove(getName()).unregister(commandMap);
        this.subcommands.clear();
    }
}
