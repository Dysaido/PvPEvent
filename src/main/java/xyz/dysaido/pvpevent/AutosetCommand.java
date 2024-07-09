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

package xyz.dysaido.pvpevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.dysaido.pvpevent.command.CommandTask;
import xyz.dysaido.pvpevent.model.AutoRun;
import xyz.dysaido.pvpevent.model.manager.AutosetManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class AutosetCommand implements CommandTask<CommandSender> {

    private final PvPEventPlugin pvpEvent;
    private final AutosetManager manager;
    public AutosetCommand(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.manager = pvpEvent.getAutosetManager();
    }
    @Override
    public boolean apply(CommandSender sender, String[] args) {
        /*String name = args[0];
        String arenaName = args[1];
        String dateC = args[2];

        String command = "eco give {sender} 10000";
        String broadcast = "EDIT CONFIG (autosets.yml)";

        manager.withAction(name).then(run -> {
            run.setArenaName(arenaName);
            run.setCommand(command);
            run.setBroadcast(broadcast);
            run.setDate(parseTime(dateC).getTime());
            manager.getSerializer().append(run);
        });*/
        sender.sendMessage(ChatColor.GOLD + "BETA");
        return false;
    }

    private Date parseTime(String inputTime) {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat ampmFormat = new SimpleDateFormat("h:mm a");

        try {
            return hourFormat.parse(inputTime);
        } catch (ParseException e1) {
            try {
                return ampmFormat.parse(inputTime);
            } catch (ParseException e2) {
                e1.addSuppressed(e2);
                Bukkit.getLogger().log(Level.WARNING, "Error:", e1);
                return null;
            }
        }
    }
}
