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

import org.bukkit.command.CommandSender;
import xyz.dysaido.pvpevent.util.BukkitHelper;
import xyz.dysaido.pvpevent.util.Logger;

public class SubCommand<S extends CommandSender> {

    private final CommandInfo info;
    private CommandTask<S> command = (sender, args) -> true;
    private String alias = "";
    private String perm = "";

    public SubCommand(CommandInfo info) {
        this.info = info;
    }

    public SubCommand<S> setCommand(CommandTask<S> task) {
        this.command = task;
        return this;
    }

    void execute(S sender, String[] args) {
        if (command != null) {
            Logger.debug(info.getName(), String.format("perm: %s, alias: %s, executing...", perm, alias));
            if (!command.apply(sender, args)) {
                sender.sendMessage(BukkitHelper.colorize(String.format("%s &5- %s", info.getUsage(), info.getDescription())));
            }
        }
    }

    public SubCommand<S> setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return info.getName();
    }

    public String getPerm() {
        return perm;
    }

    public SubCommand<S> setPerm(String perm) {
        this.perm = perm;
        return this;
    }
}
