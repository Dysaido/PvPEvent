package xyz.dysaido.pvpevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.api.model.Match;
import xyz.dysaido.pvpevent.command.CommandInfo;
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

    public String convertWithStream(Map<?, ?> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public boolean isActiveMatch() {
        return mainMatch != null && !mainMatch.isOver();
    }

    private void sendEventCommandsHelp(CommandSender sender) {
        String eventTitle = Settings.IMP.COMMAND.EVENT_TITLE;
        sender.sendMessage(BukkitHelper.colorize(eventTitle));

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

    private static boolean sendStaffMsg(CommandSender sender) {
        sender.sendMessage(BukkitHelper.colorize("&5PvPEvent Staff Commands"));
        Arrays.stream(CommandInfo.values()).forEach(info -> {
            sender.sendMessage(BukkitHelper.colorize(String.format("%s &d- %s", info.getUsage(), info.getDescription())));
        });
        return true;
    }

    private void registerCommands() {
        parentCommand.register(CommandInfo.NONE, SubCommand::new)
                .setCommand((sender, args) -> {
                    sendEventCommandsHelp(sender);
                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register(CommandInfo.HELP, SubCommand::new)
                .setCommand((sender, args) -> sendStaffMsg(sender))
                .setPerm(Settings.IMP.PERMISSION.COMMAND_HELP);
        parentCommand.register(CommandInfo.JOIN, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (!(sender instanceof Player)) return false;
                    Player player = (Player) sender;
                    if (isActiveMatch()) {
                        mainMatch.join(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }

                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register(CommandInfo.LEAVE, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (!(sender instanceof Player)) return false;
                    Player player = (Player) sender;
                    if (isActiveMatch()) {
                        mainMatch.leave(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }
                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register(CommandInfo.SPECTATE, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (!(sender instanceof Player)) return false;
                    Player player = (Player) sender;
                    if (isActiveMatch()) {
                        mainMatch.leave(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }
                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register(CommandInfo.VIEW, SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage("Arena: ");
                    sender.sendMessage(ChatColor.AQUA + convertWithStream(arenaManager.getAll()));
                    sender.sendMessage("Kit: ");
                    sender.sendMessage(ChatColor.AQUA + convertWithStream(kitManager.getAll()));

                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);

        parentCommand.register(CommandInfo.HOST, SubCommand::new)
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
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + String.format("%s wasn't saved by anyone!", arenaName));
                            }
                            //args[0] args[1]
                            //[name]  [broadcast]
                        }
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_HOST);
        parentCommand.register(CommandInfo.STOP, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (isActiveMatch()) {
                        mainMatch.onDestroy();
                        sender.sendMessage(ChatColor.GREEN + "Event has been stopped!");
                        BukkitHelper.broadcast(Settings.IMP.MESSAGE.MATCH_STOP_TEXT.replace("{executor}", sender.getName()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There isn't event!");
                    }
                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_STOP);
        parentCommand.register(CommandInfo.RELOAD, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 1) {
                        String argument = args[0].toLowerCase(Locale.ROOT);
                        switch (argument) {
                            case "full":
                                reload();
                                sender.sendMessage(ChatColor.GREEN + "Full plugin reloaded!");
                                break;
                            case "config":
                                reloadConfig();
                                sender.sendMessage(ChatColor.GREEN + "Configuration file reloaded!");
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_RELOAD);

        parentCommand.register(CommandInfo.KICK, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 1) {
                        String argument = args[0];
                        Player player = Bukkit.getServer().getPlayer(argument);
                        if (player != null && isActiveMatch()) {
                            UUID identifier = player.getUniqueId();
                            mainMatch.leave(identifier);
                            BukkitHelper.broadcast(ChatColor.RED + String.format("%s was kicked from PvPEvent by %s!", argument, sender.getName()));
                        } else {
                            sender.sendMessage(ChatColor.RED + String.format("%s didn't join to the event!", argument));
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_PUNISHMENT);
        parentCommand.register(CommandInfo.BAN, SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage("ban");
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_PUNISHMENT);
        parentCommand.register(CommandInfo.UNBAN, SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage("unban");
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_PUNISHMENT);

        parentCommand.register(CommandInfo.CREATEARENA, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 1) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (arenaManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This arena has been registrated!");
                        } else {
                            Arena arena = arenaManager.getOrMake(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s arena has been created!", arena.getIdentifier()));
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ARENA);
        parentCommand.register(CommandInfo.EDITARENA, SubCommand::new)
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
                                return false;
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
                                    player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
                                    break;
                                case "setpos1":
                                    arena.setPos1(player.getLocation());
                                    player.sendMessage(ChatColor.GREEN + "Pos1 has been set!");
                                    player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
                                    break;
                                case "setpos2":
                                    arena.setPos2(player.getLocation());
                                    player.sendMessage(ChatColor.GREEN + "Pos2 has been set!");
                                    player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
                                    break;
                                case "setkit":
                                    if (args.length == 3) {
                                        String kitname = args[2];
                                        arena.setKitName(kitname);
                                        player.sendMessage(ChatColor.GREEN + "IMPORTANT - When adding a kit to the arena, it is important that the kit name was added, if you want to use this kit in the event, then create a kit with such a name!");
                                        
                                        player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
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
                                            player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setcapacity [integer]");
                                    }
                                    break;
                                case "setmincapacity":
                                    if (args.length == 3) {
                                        String minCapacity = args[2];
                                        if (NumericParser.ensureInteger(minCapacity)) {
                                            arena.setMinCapacity(Math.max(Integer.parseInt(minCapacity), 1));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for min-capacity!", minCapacity));
                                            player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setmincapacity [integer]");
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
                                            player.sendMessage(ChatColor.GREEN + "Use: /event editarena [name] save");
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena setfightcountdown [integer, 1 = 1second, 30 = 30second]");
                                    }
                                    break;
                                default:
                                    return sendStaffMsg(sender);

                            }
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ARENA);
        parentCommand.register(CommandInfo.DELARENA, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 1) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!arenaManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This arena hasn't been registered!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event delarena [name]");
                        } else {
                            Arena arena = arenaManager.remove(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s arena has been deleted!", arena.getIdentifier()));
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ARENA);
        parentCommand.register(CommandInfo.CREATEKIT, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 1) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (kitManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This kit has been registrated!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event createkit [name]");
                        } else {
                            Kit<Player> kit = kitManager.getOrMake(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s kit has been created!", kit.getName()));
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_KIT);
        parentCommand.register(CommandInfo.EDITKIT, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 2) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!kitManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "Kit hasn't been registrated!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event editkit [name] setinventory");
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(ChatColor.RED+ "You don't have permission to perform this!");
                                return false;
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
                                return sendStaffMsg(sender);
                            }
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_KIT);
        parentCommand.register(CommandInfo.DELKIT, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length == 1) {
                        String name = args[0].toLowerCase(Locale.ROOT);
                        if (!kitManager.isLoaded(name)) {
                            sender.sendMessage(ChatColor.RED + "This kit hasn't been registered!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event createkit [name]");
                        } else {
                            Kit<Player> kit = kitManager.remove(name);
                            sender.sendMessage(ChatColor.GREEN + String.format("%s kit has been deleted!", kit.getName()));
                        }
                        return true;
                    }
                    return false;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_KIT);
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
    public void reloadConfig() {
        Settings.IMP.reload(configFile);
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
