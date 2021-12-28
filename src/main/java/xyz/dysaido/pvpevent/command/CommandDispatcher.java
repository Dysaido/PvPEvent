package xyz.dysaido.pvpevent.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import xyz.dysaido.pvpevent.PvPEvent;
import xyz.dysaido.pvpevent.command.impl.EventCommand;
import xyz.dysaido.pvpevent.command.impl.EventsCommand;
import xyz.dysaido.pvpevent.util.Reflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {
    private static CommandDispatcher instance;
    private final CommandMap commandMap;
    private final Map<String, Command> knownCommands = new HashMap<>();

    public static CommandDispatcher getInstance() {
        return instance;
    }

    private CommandDispatcher(PvPEvent plugin) {
        this.commandMap = plugin.getCommandMap();

        register(new EventCommand(plugin));
        register(new EventsCommand(plugin));

    }

    public static void enable(PvPEvent plugin) {
        if (instance == null) {
            instance = new CommandDispatcher(plugin);
        } else {
            throw new RuntimeException("Command has already been created!");
        }
    }

    public Command getCommand(String name) {
        return this.knownCommands.get(name);
    }

    public void register(Command command) {
        this.knownCommands.put(command.getName(), command);
        this.commandMap.register("event", command);
    }

    public void unregisterAll() {
        Field knownCommandsField = Reflection.getField(SimpleCommandMap.class, "knownCommands");
        Map<String, Command> knownCommands = Reflection.fetch(this.commandMap, knownCommandsField);
        knownCommands.values().removeAll(this.knownCommands.values());
        this.knownCommands.values().forEach(command -> command.unregister(this.commandMap));
        this.knownCommands.clear();
    }

    public void unregister(Command command) {
        Field knownCommandsField = Reflection.getField(SimpleCommandMap.class, "knownCommands");
        Map<String, Command> knownCommands = Reflection.fetch(this.commandMap, knownCommandsField);
        knownCommands.remove(command.getName()).unregister(this.commandMap);
        this.knownCommands.remove(command.getName());
    }
}
