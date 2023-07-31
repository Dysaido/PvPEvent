package xyz.dysaido.pvpevent.util;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandMapUtil {

    private static final Field COMMAND_MAP;
    private static final Field KNOWN_COMMANDS;

    static {
        COMMAND_MAP = Reflection.findField(SimplePluginManager.class, SimpleCommandMap.class);
        KNOWN_COMMANDS = Reflection.findField(SimpleCommandMap.class, "knownCommands");
    }

    public static SimpleCommandMap getCommandMap(Server server) {
        return Reflection.fetch(server.getPluginManager(), COMMAND_MAP);
    }

    public static Map<String, Command> getKnownCommands(SimpleCommandMap commandMap) {
        return Reflection.fetch(commandMap, KNOWN_COMMANDS);
    }
}
