package xyz.dysaido.onevsonegame.util;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.setting.Settings;

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
        BaseComponent[] components = translate(text);
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                player.spigot().sendMessage(components);
            } catch (Throwable throwable) {
                Logger.warning("Format", "Your server does not supported clickable message, please upgrade spigot/paper!");
                broadcast(text);
            }
        }
    }

    public static BaseComponent[] translate(String... strings) {
        ComponentBuilder componentBuilder = new ComponentBuilder("");

        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                componentBuilder.append("\n");
            }
            componentBuilder.append(colored(strings[i]));
            setupEvents(componentBuilder);
        }
        return componentBuilder.create();
    }

    private static void setupEvents(ComponentBuilder componentBuilder) {
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(colored(Settings.CLICKABLE_MESSAGE))));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/event:event join"));
    }

}
