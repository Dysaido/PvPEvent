package xyz.dysaido.pvpevent.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.util.BukkitHelper;
import xyz.dysaido.pvpevent.util.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCommand extends Command {

    private static final String TAG = "CommandManager";
    protected final PvPEventPlugin pvpEvent;
    protected final Map<String, SubCommand<CommandSender>> subcommands = new HashMap<>();

    public AbstractCommand(PvPEventPlugin pvpEvent, String name) {
        super(name);
        this.pvpEvent = pvpEvent;
    }

    public SubCommand<CommandSender> register(String name, Function<String, SubCommand<CommandSender>> event) {
        if (this.subcommands.containsKey(name)) {
            Logger.error(TAG, String.format("RegisterError - MainCommand: %s, subcommand: %s", getName(), name));
            throw new RuntimeException("You cannot register same command that registered");
        } else {
            Logger.debug(TAG, String.format("Register - MainCommand: %s, subcommand: %s", getName(), name));
            SubCommand<CommandSender> subcommand = event.apply(name);
            this.subcommands.put(name.toLowerCase(), subcommand);
            return subcommand;
        }
    }

    public void unregister(String name) {
        Logger.debug(TAG, String.format("Unregister - MainCommand: %s, subcommand: %s", getName(), name));
        this.subcommands.remove(name);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0 && subcommands.containsKey("")) {
            subcommands.get("").execute(sender, args);
            return true;
        }

        String arrToStr = String.join(" ", args).toLowerCase();
        Optional<Map.Entry<String, SubCommand<CommandSender>>> matchedCommand =
                subcommands.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals(""))
                        .filter(entry -> {
                            String alias = entry.getValue().getAlias();
                            boolean aliasCheck = !alias.isEmpty() && arrToStr.startsWith(entry.getValue().getAlias());
                            return arrToStr.startsWith(entry.getKey()) || aliasCheck;
                        })
                        .findAny();
        if (matchedCommand.isPresent()) {
            SubCommand<CommandSender> subcommand = matchedCommand.get().getValue();
            if (isAuthorized(sender, subcommand)) {
                String[] param = args.length == 0 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
                subcommand.execute(sender, param);
            } else {
                sender.sendMessage(MESSAGE.NO_PERMISSION.format());
            }
        } else
            sender.sendMessage(ChatColor.RED + "Unknown command. Please try /event or /event help.");
        return true;
    }

    protected boolean isAuthorized(CommandSender sender, SubCommand<CommandSender> subcommand) {
        return sender.hasPermission(subcommand.getPerm()) || sender.isOp() || sender.hasPermission(Settings.IMP.PERMISSION.COMMAND_ADMIN);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String argument = args[0];
            String argumentLowered = argument.toLowerCase(Locale.ROOT);
            return Stream.concat(
                            Stream.of("join", "leave", "spectate", "view", "help"),
                            sender.hasPermission(Settings.IMP.PERMISSION.COMMAND_ADMIN) ? Stream.of("autoset", "host",
                                    "createarena", "editarena", "delarena",
                                    "createkit", "editkit", "delkit",
                                    "stop", "reload", "kick", "ban", "unban"
                            ) : Stream.empty()
                    )
                    .filter(s -> s.startsWith(argumentLowered))
                    .collect(Collectors.toList());
            //return StringUtil.copyPartialMatches(args[0], subcommands.keySet(), new ArrayList<>(subcommands.size()));
        } else {
            if (args.length >= 2) {
                String subcommandLowered = args[0].toLowerCase();
                if (args.length == 3 && subcommandLowered.equals("editarena")) {
                    return Stream.of(
                            "save", "setlobby", "setpos1", "setpos2",
                                    "setkit", "setcapacity", "setqueuecountdown", "setfightcountdown"
                            )
                            .filter(s -> s.startsWith(args[2].toLowerCase(Locale.ROOT)))
                            .collect(Collectors.toList());
                } else if (subcommandLowered.equals("host")
                        || subcommandLowered.equals("editarena")
                        || subcommandLowered.equals("earena")
                        || subcommandLowered.equals("delarena")
                        || subcommandLowered.equals("autoset")) {
                    List<String> arenas = new ArrayList<>(pvpEvent.getArenaManager().getAll().keySet());
                    return StringUtil.copyPartialMatches(args[1], arenas, new ArrayList<>(arenas.size()));
                } else if (subcommandLowered.equals("editkit")
                        || subcommandLowered.equals("ekit")
                        || subcommandLowered.equals("delkit")) {
                    List<String> kits = new ArrayList<>(pvpEvent.getKitManager().getAll().keySet());
                    return StringUtil.copyPartialMatches(args[1], kits, new ArrayList<>(kits.size()));
                }
                return super.tabComplete(sender, alias, args);
            }
            return ImmutableList.of();
        }
    }

    public enum MESSAGE {
        NO_PERMISSION("&cYou don't have permission to perform this command!"),
        ONLY_CONSOLE("&cYou don't have permission to perform this command!"),
        ONLY_PLAYER("&cConsole don't authorized to perform this command, because you are not an entity!");

        private final String msg;

        MESSAGE(String msg) {
            this.msg = msg;
        }

        public String format() {
            return BukkitHelper.colorize(msg);
        }

    }
}