package xyz.dysaido.onevsonegame.command.impl;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.arena.Arena;
import xyz.dysaido.onevsonegame.command.BaseCommand;
import xyz.dysaido.onevsonegame.match.MatchHandler;
import xyz.dysaido.onevsonegame.match.MatchState;
import xyz.dysaido.onevsonegame.match.MatchType;
import xyz.dysaido.onevsonegame.menu.MenuManager;
import xyz.dysaido.onevsonegame.arena.ArenaCache;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.*;
import java.util.stream.Collectors;

public class EventsCommand extends BaseCommand {

    private static ArenaCache arenaCache;
    private final List<String> arguments = Arrays.asList(
            "stop",
            "list",
            "menu",
            "reload",
            "create",
            "lobby",
            "spawn",
            "spawn1",
            "spawn2",
            "inventory",
            "save",
            "host"
    );

    public EventsCommand(OneVSOneGame plugin) {
        super(plugin, "events");
        setPermission(Settings.COMMAND_EDITOR_PERMISSION);
        setPermissionMessage(MESSAGE.NO_PERMISSION.format());
    }

    @Override
    public void handle(CommandSender sender, String label, String[] args) {
        if (args.length != 0) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "stop":
                    plugin.getMatchHandler().getMatch().ifPresent(baseMatch -> {
                        baseMatch.setState(MatchState.ENDING);
                        sender.sendMessage(Format.colored("&aSuccess stop!"));
                    });
                    break;
                case "list":
                    sender.sendMessage(Format.colored("&e&lEvents:\n"));
                    plugin.getArenaManager().getArenas().stream().map(Arena::getName).forEach(s -> {
                        sender.sendMessage(Format.colored("&7" + s));
                    });
                    break;
                case "menu":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        MenuManager.getInstance().getMainMenu().open(player);
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "reload":
                    plugin.reloadConfig();
                    sender.sendMessage(Format.colored("&aSuccess reload!"));
                    break;
                case "create":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (args.length > 1) {
                            arenaCache = new ArenaCache(args[1]);
                            player.sendMessage("Create");
                        } else {
                            sendHelp(sender);
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "lobby":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (arenaCache == null) {
                            player.sendMessage("Create event, please");
                        } else {
                            arenaCache.setLobby(player.getLocation());
                            player.sendMessage("Lobby");
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "spawn":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (arenaCache == null) {
                            player.sendMessage("Create event, please");
                        } else {
                            arenaCache.setSpawn(player.getLocation());
                            player.sendMessage("Spawn");
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "spawn1":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (arenaCache == null) {
                            player.sendMessage("Create event, please");
                        } else {
                            arenaCache.setSpawn1(player.getLocation());
                            player.sendMessage("Spawn1");
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "spawn2":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (arenaCache == null) {
                            player.sendMessage("Create event, please");
                        } else {
                            arenaCache.setSpawn2(player.getLocation());
                            player.sendMessage("Spawn2");
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "inventory":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (arenaCache == null) {
                            player.sendMessage("Create event, please");
                        } else {
                            PlayerInventory inventory = player.getInventory();
                            arenaCache.setContents(inventory.getContents());
                            arenaCache.setArmor(inventory.getArmorContents());
                            player.sendMessage("Inventory");
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "save":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (arenaCache == null) {
                            player.sendMessage("Create event, please");
                        } else {
                            plugin.getArenaManager().save(arenaCache);
                            arenaCache = null;
                            player.sendMessage("Save");
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "host":
                    if (args.length > 1) {
                        String name = args[1];
                        Arena arena = plugin.getArenaManager().get(name);
                        if (arena != null) {
                            MatchHandler handler = plugin.getMatchHandler();
                            if (handler.host(MatchType.SOLOS, arena)) {
                                sender.sendMessage("Successful host");
                            } else {
                                sender.sendMessage("The match has already been created. Please, you have to destroy previous match that you want to create a new match.");
                            }
                        } else {
                            sender.sendMessage("Couldn't find this event");
                        }
                    } else {
                        sendHelp(sender);
                    }
                    break;
                default:
                    sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
    }

    private void sendHelp(CommandSender sender) {
        Settings.COMMAND_HELP_EVENTS.stream().map(Format::colored).forEach(sender::sendMessage);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>(arguments.size()));
        } else {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("host")) {
                    List<String> arenas = plugin.getArenaManager().getArenas().stream().map(Arena::getName).collect(Collectors.toList());
                    return StringUtil.copyPartialMatches(args[1], arenas, new ArrayList<>(arenas.size()));
                }
                return super.tabComplete(sender, alias, args);
            }
            return ImmutableList.of();
        }
    }
}
