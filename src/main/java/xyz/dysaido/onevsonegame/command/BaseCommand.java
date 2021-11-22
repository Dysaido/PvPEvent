package xyz.dysaido.onevsonegame.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sun.rmi.runtime.Log;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.util.Format;
import xyz.dysaido.onevsonegame.util.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseCommand<P extends JavaPlugin> extends Command {

    protected final P plugin;
    private final Map<String, SubAdapter> commandsMap = new HashMap<>();
    private Map<String, BaseCommand<P>> loadedCommands = new HashMap<>();
    public static <T extends BaseCommand<?>> T getCommand(Class<T> clazz) {

        return null;
    }

    public BaseCommand(P plugin, String name, String description, String usage, List<String> aliases) {
        super(name, Format.colored(description), Format.colored(usage), aliases);
        this.plugin = plugin;
    }

    public void loadCommands() {
        Arrays.stream(getClass().getDeclaredClasses()).parallel().filter(SubAdapter.class::isAssignableFrom).forEach(clazz -> {
            try {
                registerCommand(clazz.asSubclass(SubAdapter.class).getDeclaredConstructor().newInstance());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Cannot load commands");
            }
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Stream<SubAdapter> stream = commandsMap.values().stream();
        stream.sorted(Comparator.comparing(SubAdapter::getName)).forEach(adapter -> {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase(adapter.name) || adapter.aliases.contains(args[0])) {
                    if (sender instanceof Player) {
                        if (adapter.onlyConsole) {
                            sender.sendMessage(MESSAGE.NO_PERMISSION.format());
                            return;
                        }
                        if (adapter.onlyOp && !sender.isOp()) {
                            sender.sendMessage(MESSAGE.NO_PERMISSION.format());
                            return;
                        }
                        if (adapter.permission.length() > 0) {
                            if (!sender.hasPermission(adapter.permission)) {
                                sender.sendMessage(MESSAGE.NO_PERMISSION.format());
                                return;
                            }
                        }
                    } else {
                        if (adapter.onlyPlayer) {
                            sender.sendMessage(MESSAGE.NO_PERMISSION.format());
                            return;
                        }
                    }
                    List<String> list = new ArrayList<>(Arrays.asList(args));
                    list.remove(0);
                    adapter.execute(sender, list.toArray(new String[0]));
                }
            } else {
                sendHelp(adapter, sender);
            }
        });
        return true;
    }

    protected static void sendHelp(SubAdapter adapter, CommandSender sender) {
        String permission = adapter.permission.length() > 0 ? adapter.permission : "none";
        String description = adapter.description;
        sender.sendMessage(Format.colored("&7" + adapter.usage + " &f" + description + " &7(&a" + permission + "&7)"));

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return commandsMap.values()
                    .parallelStream()
                    .map(SubAdapter::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        } else if (args.length >= 2) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }

    public void registerCommand(SubAdapter command) {
        Logger.information("BaseCommand", "Loaded command : " + command.getName());
        commandsMap.put(command.getName(), command);
    }

    public void unregisterCommand(SubAdapter command) {
        commandsMap.remove(command.getName());
    }

    public enum MESSAGE {
        NO_PERMISSION("&cYou don't have permission to perform this command!"),
        ONLY_CONSOLE("&cVanilla entity don't authorized to perform this command!"),
        ONLY_PLAYER("&cConsole don't authorized to perform this command, because you are not an entity!");

        private final String msg;

        MESSAGE(String msg) {
            this.msg = msg;
        }

        public String format() {
            return ChatColor.translateAlternateColorCodes('&', msg);
        }

    }

    protected abstract static class SubAdapter {
        protected final OneVSOneGame plugin = JavaPlugin.getPlugin(OneVSOneGame.class);
        private final String name;
        private final String usage;
        private final String permission;
        private final List<String> aliases;
        private String description;
        private final boolean onlyPlayer;
        private final boolean onlyConsole;
        private final boolean onlyOp;

        public SubAdapter() {
            if (getClass().isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = getClass().getAnnotation(SubCommand.class);
                name = subCommand.name();
                usage = subCommand.usage();
                permission = subCommand.permission();
                aliases = Arrays.asList(subCommand.aliases());
                description = subCommand.description();
                onlyPlayer = subCommand.onlyPlayer();
                onlyConsole = subCommand.onlyConsole();
                onlyOp = subCommand.onlyOp();
            } else {
                throw new RuntimeException("You must to add SubCommand.class annotation");
            }

        }

        protected abstract void execute(CommandSender sender, String[] args);

        public String getName() {
            return name;
        }

        public String getUsage() {
            return usage;
        }

        public String getPermission() {
            return permission;
        }

        public List<String> getAliases() {
            return aliases;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public boolean isOnlyPlayer() {
            return onlyPlayer;
        }

        public boolean isOnlyConsole() {
            return onlyConsole;
        }
    }

}
