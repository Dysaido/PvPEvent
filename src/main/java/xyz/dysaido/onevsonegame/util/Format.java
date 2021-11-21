package xyz.dysaido.onevsonegame.util;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.dysaido.onevsonegame.setting.Config;

import java.util.List;

public class Format {

    public static String colored(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void broadcast(String text) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(colored(text)));
    }

    public static void broadcast(List<String> texts) {
        texts.stream().map(Format::colored).forEach(Format::broadcast);
    }

    public static void broadcastClickable(String text) {
        try {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(translate(text)));
        } catch (Exception e) {
            Logger.error("Format", "You are using spigot, because of this you don't use clickable message, please upgrade paper!");
            broadcast(text);
        }
    }

    public static BaseComponent[] translate(String... strings) {
        ComponentBuilder componentBuilder = new ComponentBuilder("");

        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                componentBuilder.append("\n");
            }

            componentBuilder.append(colored(strings[i]));
            link(componentBuilder);
        }
        return componentBuilder.create();
    }

    private static void link(ComponentBuilder componentBuilder) {
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                Config.CLICKABLE_MESSAGE
        ))));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event:event join"));
    }

}
