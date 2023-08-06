package xyz.dysaido.pvpevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.api.model.Match;
import xyz.dysaido.pvpevent.command.ParentCommand;
import xyz.dysaido.pvpevent.command.SubCommand;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.listener.ConnectionListener;
import xyz.dysaido.pvpevent.match.Kit;
import xyz.dysaido.pvpevent.match.MatchState;
import xyz.dysaido.pvpevent.match.impl.SumoMatch;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;
import xyz.dysaido.pvpevent.model.manager.KitManager;
import xyz.dysaido.pvpevent.util.BukkitHelper;
import xyz.dysaido.pvpevent.util.NumericParser;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PvPEventPlugin implements PvPEvent {

    private final JavaPlugin plugin;
    private final File configFile;
    private final ParentCommand parentCommand;
    private final ConnectionListener connectionListener;
    private final ArenaManager arenaManager;
    private final KitManager kitManager;
    private Match<UUID> mainMatch;

    public PvPEventPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.parentCommand = new ParentCommand(this,"event");
        this.connectionListener = new ConnectionListener(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
    }

    @Override
    public void enable() {
        Settings.IMP.reload(configFile);

        parentCommand.load();
        connectionListener.load();

        arenaManager.load();
        kitManager.load();

        registerCommands();
    }

//    void test() {
//        this.mainMatch = new SumoMatch(this, "Apple", arenaManager.getIfPresent("test"))
//                                .onCreate(this, 10);
//    }
    public String convertWithStream(Map<?, ?> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public boolean isActiveMatch() {
        return mainMatch != null && mainMatch.getState() != MatchState.INACTIVE;
    }

    private void sendEventCommandsHelp(CommandSender sender) {
        String eventTitle = Settings.IMP.COMMAND.EVENT_TITLE;
        sender.sendMessage(BukkitHelper.colorize(eventTitle));
        sender.sendMessage(BukkitHelper.colorize("&r"));

        String eventJoin = Settings.IMP.COMMAND.EVENT_JOIN;
        sender.sendMessage(BukkitHelper.colorize(eventJoin));

        String eventLeave = Settings.IMP.COMMAND.EVENT_LEAVE;
        sender.sendMessage(BukkitHelper.colorize(eventLeave));

        String eventSpectate = Settings.IMP.COMMAND.EVENT_SPECTATE;
        sender.sendMessage(BukkitHelper.colorize(eventSpectate));

        String eventView = Settings.IMP.COMMAND.EVENT_VIEW;
        sender.sendMessage(BukkitHelper.colorize(eventView));

        String eventHelp = Settings.IMP.COMMAND.EVENT_HELP;
        sender.sendMessage(BukkitHelper.colorize(eventHelp));
    }

    private static final List<String> MSG_STAFF_HELP = Stream.of("\n" +
            "&c&lPvPEvent Staff Commands\n" +
            "&r" +
            "&5/event autoset&4(BETA) &d[arena-name] <broadcast> <command> <schedule> \n" +
            "&5/event host &d[arena-name] <broadcast>\n" +
            "&5/event createarena &d[arena-name]\n" +
            "&5/event editarena &d[arena-name] <save|setlobby|setpos1|setpos2|setkit|setcapacity|setqueuecountdown(second)|setfightcountdown(second)>\n" +
            "&5/event delarena &d[arena-name]\n" +

            "&5/event createkit &d[kit-name]\n" +
            "&5/event editkit &d[kit-name] setinventory (who executes the command, his inventory will be saved)\n" +
            "&5/event delkit &d[kit-name]\n" +

            "&5/event stop\n" +
            "&5/event reload &dreload this configs and some options\n" +
            "&5/event kick&4(BETA) &d[user] <reason>\n" +
            "&5/event ban&4(BETA) &d[user] <reason>\n" +
            "&5/event unban&4(BETA) &d[user]\n"
    ).map(BukkitHelper::colorize).collect(Collectors.toList());

    private void registerCommands() {
        parentCommand.register("", SubCommand::new)
                .setCommand((sender, args) -> sendEventCommandsHelp(sender))
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register("help", SubCommand::new)
                .setCommand((sender, args) -> MSG_STAFF_HELP.forEach(sender::sendMessage))
                .setAlias("?")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_HELP);
        parentCommand.register("join", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (!(sender instanceof Player)) return;
                    Player player = (Player) sender;
                    if (isActiveMatch()) {
                        mainMatch.join(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }
                })
                .setAlias("j")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register("leave", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (!(sender instanceof Player)) return;
                    Player player = (Player) sender;
                    if (isActiveMatch()) {
                        mainMatch.leave(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }
                })
                .setAlias("l")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register("spectate", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (!(sender instanceof Player)) return;
                    Player player = (Player) sender;
                    if (isActiveMatch()) {
                        mainMatch.leave(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }
                })
                .setAlias("sp")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register("host", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (isActiveMatch()) {
                        sender.sendMessage(ChatColor.RED + "You can't create event while another is running!");
                    } else {
                        if (args.length > 1) {
                            String arenaName = args[0].toLowerCase();
                            if (arenaManager.isLoaded(arenaName)) {
                                Arena arena = arenaManager.getIfPresent(arenaName);
                                String presentMsg = Arrays.stream(args)
                                                        .skip(1)
                                                        .reduce((t, u) -> t + " " + u)
                                                        .orElse("");
                                int modulo = Math.max(Settings.IMP.COUNTDOWN.BASE_CREATE_MODULO, 1);
                                this.mainMatch = new SumoMatch(this, presentMsg, arena).onCreate(this, modulo);
                            } else {
                                sender.sendMessage(ChatColor.RED + String.format("%s wasn't saved by anyone!", arenaName));
                            }
                            //args[0] args[1]
                            //[name]  [broadcast]
                        } else {
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event host [name] [broadcast]");
                        }
                    }
                })
                .setAlias("h")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_HOST);
        parentCommand.register("createarena", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 0) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (arenaManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This arena has been registrated!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event createarena [name]");
                        } else {
                            Arena arena = arenaManager.getOrMake(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s arena has been created!", arena.getIdentifier()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event createarena [name]");
                    }
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ARENA);
        parentCommand.register("delarena", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 0) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!arenaManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This arena hasn't been registered!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event delarena [name]");
                        } else {
                            Arena arena = arenaManager.remove(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s arena has been deleted!", arena.getIdentifier()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event delarena [name]");
                    }
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ARENA);
        parentCommand.register("createkit", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 0) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (kitManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This kit has been registrated!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event createkit [name]");
                        } else {
                            Kit<Player> kit = kitManager.getOrMake(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s kit has been created!", kit.getName()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event createkit [name]");
                    }
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_KIT);
        parentCommand.register("delkit", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 0) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!kitManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This kit hasn't been registered!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event createkit [name]");
                        } else {
                            Kit<Player> kit = kitManager.remove(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s kit has been deleted!", kit.getName()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event delkit [name]");
                    }
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_KIT);
        parentCommand.register("view", SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage("Arena: ");
                    sender.sendMessage(ChatColor.AQUA + convertWithStream(arenaManager.getAll()));
                    sender.sendMessage("Kit: ");
                    sender.sendMessage(ChatColor.AQUA + convertWithStream(kitManager.getAll()));
                })
                .setAlias("v")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register("editkit", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 1) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!kitManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "Kit hasn't been registrated!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event editkit [name] setinventory");
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(ChatColor.RED+ "You don't have permission to perform this!");
                                return;
                            }
                            Player player = (Player) sender;
                            Kit<Player> kit = kitManager.getIfPresent(name);
                            String option = args[1].toLowerCase();
                            if (option.equals("setinventory")) {
                                PlayerInventory inventory = player.getInventory();

                                kit.setArmor(inventory.getArmorContents());
                                kit.setContents(inventory.getContents());

                                kitManager.getSerializer().write();

                                player.sendMessage(ChatColor.GREEN + "Kit modified!");
                            } else {
                                MSG_STAFF_HELP.forEach(sender::sendMessage);
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editkit [name] setinventory");
                    }
                })
                .setAlias("ekit")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_KIT);
        parentCommand.register("editarena", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 1) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!arenaManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "Arena hasn't been registrated!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] <option>");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "Options: setlobby, setpos1, setpos2, setkit [name]");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "setcapacity [integer], setqueuecountdown [integer], setfightcountdown [integer]");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "save - it is worth saving after setting each parameter, because if you do not use it, it will not be saved in the config");
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(ChatColor.RED+ "You don't have permission to perform this!");
                                return;
                            }
                            Player player = (Player) sender;
                            Arena arena = arenaManager.getIfPresent(name);
                            String option = args[1].toLowerCase();
                            // <save|setlobby|setpos1|setpos2|setkit|setcapacity|setqueuecountdown|setfightcountdown>
                            switch (option) {
                                case "save":
                                    arenaManager.getSerializer().write();
                                    player.sendMessage(ChatColor.GREEN + "Arenas.json has been updated!");
                                    break;
                                case "setlobby":
                                    arena.setLobby(player.getLocation());
                                    player.sendMessage(ChatColor.GREEN + "Lobby has been set!");
                                    break;
                                case "setpos1":
                                    arena.setPos1(player.getLocation());
                                    player.sendMessage(ChatColor.GREEN + "Pos1 has been set!");
                                    break;
                                case "setpos2":
                                    arena.setPos2(player.getLocation());
                                    player.sendMessage(ChatColor.GREEN + "Pos2 has been set!");
                                    break;
                                case "setkit":
                                    if (args.length == 3) {
                                        String kitname = args[2];
                                        arena.setKit(kitname);
                                        player.sendMessage(ChatColor.GREEN + "IMPORTANT - When adding a kit to the arena, it is important that the kit name was added, if you want to use this kit in the event, then create a kit with such a name!");
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setkit [name]");
                                    }
                                    break;
                                case "setcapacity":
                                    if (args.length == 3) {
                                        String capacity = args[2];
                                        if (NumericParser.ensureInteger(capacity)) {
                                            arena.setCapacity(Math.max(Integer.parseInt(capacity), 1));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for capacity!", capacity));
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setcapacity [integer]");
                                    }
                                    break;
                                case "setqueuecountdown":
                                    if (args.length == 3) {
                                        String queueCountdown = args[2];
                                        if (NumericParser.ensureInteger(queueCountdown)) {
                                            arena.setQueueCountdown(Math.max(Integer.parseInt(queueCountdown), 10));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for queue countdown!", queueCountdown));
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setqueuecountdown [integer, 1 = 1second, 30 = 30second]");
                                    }
                                    break;
                                case "setfightcountdown":
                                    if (args.length == 3) {
                                        String fightCountdown = args[2];
                                        if (NumericParser.ensureInteger(fightCountdown)) {
                                            arena.setFightCountdown(Integer.parseInt(fightCountdown));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for fight countdown!", fightCountdown));
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setfightcountdown [integer, 1 = 1second, 30 = 30second]");
                                    }
                                    break;
                                default:
                                    MSG_STAFF_HELP.forEach(sender::sendMessage);
                                    break;

                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] <option>");
                    }
                })
                .setAlias("earena")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ARENA);
        parentCommand.register("stop", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (isActiveMatch()) {
                        mainMatch.onDestroy();
                        sender.sendMessage(ChatColor.GREEN + "Event has been stopped!");
                        BukkitHelper.broadcast(Settings.IMP.MESSAGE.MATCH_STOP_TEXT.replace("{executor}", sender.getName()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There isn't event!");
                    }
                })
                .setAlias("s")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_STOP);
        parentCommand.register("reload", SubCommand::new)
                .setCommand((sender, args) -> {
                    reload();
                    sender.sendMessage(ChatColor.GREEN + "Reload success!");
                })
                .setAlias("rl")
                .setPerm(Settings.IMP.PERMISSION.COMMAND_RELOAD);
        parentCommand.register("kick", SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length > 0) {
                        String argument = args[0];
                        Player player = Bukkit.getServer().getPlayer(argument);
                        if (player != null && isActiveMatch()) {
                            UUID identifier = player.getUniqueId();
                            mainMatch.leave(identifier);
                            BukkitHelper.broadcast(ChatColor.RED + String.format("%s was kicked from PvPEvent by %s!", argument, sender.getName()));
                        } else {
                            sender.sendMessage(ChatColor.RED + String.format("%s didn't join to the event!", argument));
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event kick [name]");
                    }
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_PUNISHMENT);
        parentCommand.register("ban", SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage("ban");
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_PUNISHMENT);
        parentCommand.register("unban", SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage("unban");
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_PUNISHMENT);
    }

    @Override
    public void disable() {
        parentCommand.unload();
        connectionListener.unload();
        arenaManager.unload();
        kitManager.unload();
    }

    @Override
    public void reload() {
        disable();
        enable();
    }

    @Override
    public void setMainMatch(Match<UUID> mainMatch) {
        this.mainMatch = mainMatch;
    }

    @Override
    public Optional<Match<UUID>> getMainMatch() {
        return Optional.ofNullable(mainMatch);
    }

    @Override
    public boolean hasMainMatch() {
        return mainMatch != null;
    }

    @Override
    public void reloadStorages() {
        Settings.IMP.reload(configFile);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public File getConfigFile() {
        return configFile;
    }

    @Override
    public ParentCommand getCommandManager() {
        return parentCommand;
    }

    @Override
    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    @Override
    public KitManager getKitManager() {
        return kitManager;
    }
}
