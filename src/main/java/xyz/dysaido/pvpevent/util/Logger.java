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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.dysaido.pvpevent.config.Settings;

public final class Logger {
    private final static int DEBUG = 0;
    private final static int INFORMATION = 1;
    private final static int WARNING = 2;
    private final static int ERROR = 3;
    private final static String TAG = "[PvPEvent]";

    private Logger() {}

    public static void warn(String tag, String message) {
        println(WARNING, tag, message);
    }

    public static void error(String tag, String message) {
        println(ERROR, tag, message);
    }

    public static void info(String tag, String message) {
        println(INFORMATION, tag, message);
    }

    public static void debug(String tag, String message) {
        println(DEBUG, tag, message);
    }

    private static void println(int priority, String tag, String message) {
        switch (priority) {
            case DEBUG:
                if (Settings.IMP.DEBUG) sendMessage(ChatColor.DARK_GREEN, " (DEBUG) [" + tag + "] : " + message);
                break;
            case INFORMATION:
                sendMessage(ChatColor.BLUE, " [" + tag + "] : " + message);
                break;
            case WARNING:
                sendMessage(ChatColor.GOLD, " [" + tag + "] : " + message);
                break;
            case ERROR:
                sendMessage(ChatColor.RED, " [" + tag + "] : " + message);
                break;
        }
    }

    private static void sendMessage(ChatColor color, String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(TAG + color + message);
    }
}
