package xyz.dysaido.onevsonegame.util;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

public class Format {

    public static String colored(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void broadcast(String text) {
        Bukkit.broadcastMessage(colored(text));
    }

    public static void broadcast(List<String> texts) {
        texts.stream().map(Format::colored).forEach(Format::broadcast);
    }

    public static String translate(String... strings) {
        ComponentBuilder componentBuilder = new ComponentBuilder("");

        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                componentBuilder.append("\n");
            }

            componentBuilder.append(ChatColor.translateAlternateColorCodes('&', strings[i]));
            link(componentBuilder);
        }
        BaseComponent[] components = componentBuilder.create();
        return new TextComponent(components).toLegacyText();
    }

    private static void link(ComponentBuilder componentBuilder) {
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                "&bContributor: &fDysaido#3162"
        ))));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "https://github.com/Dysaido"));
    }

}
