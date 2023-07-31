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

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
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
