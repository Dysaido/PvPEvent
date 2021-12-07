package xyz.dysaido.onevsonegame.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.dysaido.onevsonegame.setting.Settings;

public final class Logger {
    private final static int DEBUG = 0;
    private final static int INFORMATION = 1;
    private final static int WARNING = 2;
    private final static int ERROR = 3;
    private final static String TAG = "[Event]";

    private Logger() {}

    public static void warning(String tag, String message) {
        println(WARNING, tag, message);
    }

    public static void error(String tag, String message) {
        println(ERROR, tag, message);
    }

    public static void information(String tag, String message) {
        println(INFORMATION, tag, message);
    }

    public static void debug(String tag, String message) {
        println(DEBUG, tag, message);
    }

    private static void println(int priority, String tag, String message) {
        switch (priority) {
            case DEBUG:
                if (Settings.DEBUG) sendMessage(ChatColor.DARK_GREEN, " [" + tag + "] : " + message);
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
