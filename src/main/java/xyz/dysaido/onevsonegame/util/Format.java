package xyz.dysaido.onevsonegame.util;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.setting.Settings;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.IntStream;

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
                sendClickableMessage(player, components);
            } catch (ReflectiveOperationException e) {
                try {
                    player.spigot().sendMessage(components);
                } catch (Throwable throwable) {
                    Logger.warning("Format", "Your server does not supported clickable message, please upgrade spigot/paper!");
                    broadcast(text);
                }
            }
        }
    }

    private static void sendClickableMessage(Player player, BaseComponent[] components) throws ReflectiveOperationException {
        Class<?> PlayerClass = player.getClass();
        Object array = Array.newInstance(BaseComponent.class, components.length);
        IntStream.range(0, components.length).forEach(i -> Array.set(array, i, components[i]));
        /*for (int i = 0; i < components.length; i++) {
            Array.set(array, i, components[i]);
        }*/
        Class<?> arrayInput = Class.forName("[Lnet.md_5.bungee.api.chat.BaseComponent;");
        Method sendMessage = PlayerClass.getDeclaredMethod("sendMessage", arrayInput);
        sendMessage.invoke(player, array);
    }

    public static BaseComponent[] translate(String... strings) {
        ComponentBuilder componentBuilder = new ComponentBuilder("");

        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                componentBuilder.append("\n");
            }

            componentBuilder.append(colored(strings[i]));
            action(componentBuilder);
        }
        return componentBuilder.create();
    }

    private static void action(ComponentBuilder componentBuilder) {
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                Settings.CLICKABLE_MESSAGE
        ))));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event:event join"));
    }

}
