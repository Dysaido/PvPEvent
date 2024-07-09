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

package xyz.dysaido.pvpevent.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.dysaido.pvpevent.config.Settings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BukkitHelper {

    private static final String TAG = "BukkitHelper";

    public static String colorize(String text, Object... objects) {
        return ChatColor.translateAlternateColorCodes('&', String.format(text, objects));
    }

    public static void broadcast(String text) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(colorize(text)));
    }

    public static void broadcastClickable(String text) {
        BaseComponent[] legacyText = TextComponent.fromLegacyText(colorize(Settings.IMP.MESSAGE.MATCH_HOVER_TEXT));

        ComponentBuilder cbuilder = translate(text);
        cbuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, legacyText));
        cbuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Settings.IMP.MESSAGE.MATCH_CLICK_TEXT));

        BaseComponent[] shitText = cbuilder.create();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(shitText);
        }
    }


    public static ComponentBuilder translate(String... strings) {
        ComponentBuilder componentBuilder = new ComponentBuilder("");

        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                componentBuilder.append("\n");
            }
            componentBuilder.append(colorize(strings[i]));
        }
        return componentBuilder;
    }

    /**
     *
     * @param current The current value like the numerator
     * @param max The total value like denominator
     * @param totalBars The total char count what we want to see from the progress
     * @param symbol The symbol that we want to see
     * @param successful Positive progressbar
     * @param unsuccessful Unloaded progressbar
     * @return progressbar
     */
    public static String progressBar(int current, int max, int totalBars, char symbol, ChatColor successful, ChatColor unsuccessful) {
        float percent = (float) current / max;

        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);

        StringBuilder sb = new StringBuilder();
        sb.append(successful);
        for (int i = 0; i < progressBars; i++) {
            sb.append(symbol);
        }
        sb.append(unsuccessful);
        for (int i = 0; i < leftOver; i++) {
            sb.append(symbol);
        }
        return sb.toString();
    }
}
