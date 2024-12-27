/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import xyz.dysaido.pvpevent.match.impl.DuelMatch;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;
import xyz.dysaido.pvpevent.model.manager.KitManager;
import xyz.dysaido.pvpevent.model.manager.UserManager;
import xyz.dysaido.pvpevent.util.BukkitHelper;
import xyz.dysaido.pvpevent.util.CustomLocation;
import xyz.dysaido.pvpevent.util.NumericParser;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PvPEventPlugin implements PvPEvent {

    private final JavaPlugin plugin;
    private final File configFile;
    private final ParentCommand parentCommand;
    private final ConnectionListener connectionListener;
    private final ArenaManager arenaManager;
    private final KitManager kitManager;
    private final UserManager userManager;
    private Match<UUID> mainMatch;

    public PvPEventPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.parentCommand = new ParentCommand(this,"event");
        this.connectionListener = new ConnectionListener(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.userManager = new UserManager(this);
    }

    @Override
    public void enable() {
        Settings.IMP.reload(configFile);

        parentCommand.load();
        connectionListener.load();

        arenaManager.load();
        kitManager.load();
        userManager.load();

        registerCommands();
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

        String eventView = Settings.IMP.COMMAND.EVENT_TOPLIST;
        sender.sendMessage(BukkitHelper.colorize(eventView));

        String eventHelp = Settings.IMP.COMMAND.EVENT_HELP;
        sender.sendMessage(BukkitHelper.colorize(eventHelp));
    }

    private static boolean sendStaffMsg(CommandSender sender) {
        sender.sendMessage(BukkitHelper.colorize("&5PvPEvent Staff Commands"));
        Arrays.stream(CommandInfo.getStaffs()).forEach(info -> {
            sender.sendMessage(BukkitHelper.colorize(String.format("%s &d- %s", info.getUsage(), info.getDescription())));
        });
        return true;
    }

    private void registerCommands() {
        // event vote - permission, legyen egy szavazasi mennyiseg aminel elindulhat az event
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
                        mainMatch.spectate(player.getUniqueId());
                    } else {
                        sender.sendMessage(BukkitHelper.colorize(Settings.IMP.COMMAND.DEFAULT_NO_EVENT));
                    }
                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);
        parentCommand.register(CommandInfo.TOPLIST, SubCommand::new)
                .setCommand((sender, args) -> {
                    if (args.length != 0) return false;
                    sender.sendMessage("TopList: ");
                    userManager.getToplist(10)
                            .forEach(user -> {
                                sender.sendMessage(BukkitHelper.colorize(String.format(
                                        "&6%s &e- wins: %d, kills: %d, deaths: %d",
                                        user.getName(), user.getWins(), user.getKills(), user.getDeaths())));
                            });

                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_DEFAULT);

        parentCommand.register(CommandInfo.VIEW, SubCommand::new)
                .setCommand((sender, args) -> {
                    sender.sendMessage(BukkitHelper.colorize("&4&lArenas: "));
                    arenaManager.getAll().forEach((name, arena) -> {
                        sender.sendMessage(BukkitHelper.colorize("&7 Name: &c%s", name));
                        sender.sendMessage(BukkitHelper.colorize("&7 -lobby: &c%s", arena.getLobby() == null ? "false" : "true"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -pos1: &c%s", arena.getPos1() == null ? "false" : "true"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -pos2: &c%s", arena.getPos2() == null ? "false" : "true"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -invtoggle: &c%s", arena.isToggleInventory() ? "true" : "false"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -combomode: &c%s", arena.isComboMode() ? "true" : "false"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -kit: &c%s", arena.getKitName() == null || arena.getKitName().isEmpty() ? "false" : "true"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -mincapacity: &c%s", arena.getMinCapacity()));
                        sender.sendMessage(BukkitHelper.colorize("&7 -maxcapacity: &c%s", arena.getCapacity()));
                        sender.sendMessage(BukkitHelper.colorize("&7 -queueCT: &c%s", arena.getQueueCountdown()));
                        sender.sendMessage(BukkitHelper.colorize("&7 -fightCT: &c%s", arena.getFightCountdown()));
                        sender.sendMessage("--------------------");
                    });
                    sender.sendMessage(BukkitHelper.colorize("&6&lKits: "));
                    kitManager.getAll().forEach((name, kit) -> {
                        sender.sendMessage(BukkitHelper.colorize("&7 Name: &e%s", name));
                        sender.sendMessage(BukkitHelper.colorize("&7 -contents: &e%s", kit.getContents() == null ? "false" : "true"));
                        sender.sendMessage(BukkitHelper.colorize("&7 -armors: &e%s", kit.getArmor() == null ? "false" : "true"));
                        sender.sendMessage("--------------------");
                    });
                    return true;
                })
                .setPerm(Settings.IMP.PERMISSION.COMMAND_ADMIN);
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
                                if (!arena.shouldTeleport()) {
                                    sender.sendMessage(BukkitHelper.colorize("&4&lYou cannot start any events while locations do not set!"));
                                    return true;
                                }
                                this.mainMatch = new DuelMatch(this, presentMsg, arena).onCreate(this, Settings.IMP.COUNTDOWN.BASE_CREATE_ANNOUNCE);
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
                            sender.sendMessage(ChatColor.RED + "Arena hasn't been registered!");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] <option>");
                            sender.sendMessage(ChatColor.DARK_PURPLE + "Options: setlobby, setpos1, setpos2, setkit [name]");
                            sender.sendMessage(ChatColor.DARK_PURPLE + " mincapacity [integer], capacity [integer], queuetimer [integer], fighttimer [integer]");
                            sender.sendMessage(ChatColor.DARK_PURPLE + " invtoggle [true|false], combomode [true|false]");
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(ChatColor.RED+ "You don't have permission to perform this!");
                                return false;
                            }
                            Player player = (Player) sender;
                            Arena arena = arenaManager.getIfPresent(name);
                            String option = args[1].toLowerCase();
                            // <setlobby|setpos1|setpos2|setkit|mincapacity|capacity|queuetimer|fighttimer|invtoggle|combomode>
                            switch (option) {
                                case "setlobby":
                                    arena.setLobby(CustomLocation.of(player.getLocation()));
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + "It sets the position where you are currently standing!");
                                    player.sendMessage(ChatColor.GREEN + "Lobby has been set!");
                                    arenaManager.getSerializer().write();
                                    break;
                                case "setpos1":
                                    arena.setPos1(CustomLocation.of(player.getLocation()));
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + "It sets the position where you are currently standing!");
                                    player.sendMessage(ChatColor.GREEN + "Pos1 has been set!");
                                    arenaManager.getSerializer().write();
                                    break;
                                case "setpos2":
                                    arena.setPos2(CustomLocation.of(player.getLocation()));
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + "It sets the position where you are currently standing!");
                                    player.sendMessage(ChatColor.GREEN + "Pos2 has been set!");
                                    arenaManager.getSerializer().write();
                                    break;
                                case "setkit":
                                    if (args.length == 3) {
                                        String kitname = args[2];
                                        arena.setKitName(kitname);
                                        player.sendMessage(ChatColor.GREEN + "IMPORTANT - When adding a kit to the arena, it is important that the kit name was added, if you want to use this kit in the event, then create a kit with such a name!");

                                        arenaManager.getSerializer().write();
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] setkit [name]");
                                    }
                                    break;
                                case "capacity":
                                    if (args.length == 3) {
                                        String capacity = args[2];
                                        if (NumericParser.ensureInteger(capacity)) {
                                            arena.setCapacity(Math.max(Integer.parseInt(capacity), 1));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for capacity!", capacity));

                                            arenaManager.getSerializer().write();
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] capacity [integer]");
                                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Sets the arena's maximum capacity.");
                                    }
                                    break;
                                case "mincapacity":
                                    if (args.length == 3) {
                                        String minCapacity = args[2];
                                        if (NumericParser.ensureInteger(minCapacity)) {
                                            arena.setMinCapacity(Math.max(Integer.parseInt(minCapacity), 1));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for min-capacity!", minCapacity));

                                            arenaManager.getSerializer().write();
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] mincapacity [integer]");
                                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Sets the required capacity for the arena's start.");
                                    }
                                    break;
                                case "queuetimer":
                                    if (args.length == 3) {
                                        String queueCountdown = args[2];
                                        if (NumericParser.ensureInteger(queueCountdown)) {
                                            arena.setQueueCountdown(Math.max(Integer.parseInt(queueCountdown), 10));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for queue countdown!", queueCountdown));

                                            arenaManager.getSerializer().write();
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] queuetimer [integer, 1 = 1second, 30 = 30second]");
                                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Sets the connection countdown in seconds.");
                                    }
                                    break;
                                case "fighttimer":
                                    if (args.length == 3) {
                                        String fightCountdown = args[2];
                                        if (NumericParser.ensureInteger(fightCountdown)) {
                                            arena.setFightCountdown(Integer.parseInt(fightCountdown));
                                            player.sendMessage(ChatColor.GREEN + String.format("%s has been set for fight countdown!", fightCountdown));

                                            arenaManager.getSerializer().write();
                                        } else {
                                            player.sendMessage(ChatColor.RED + "This is not integer!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] fighttimer [integer, 1 = 1second, 30 = 30second]");
                                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Sets the time elapsed between battles.");
                                    }
                                    break;
                                case "invtoggle":
                                    if (args.length == 3) {
                                        String tInvBool = args[2];
                                        if (Boolean.parseBoolean(tInvBool)) {
                                            player.sendMessage(ChatColor.GREEN + String.format("%b has been set for toggle inventory!", true));
                                            arena.setToggleInventory(true);
                                        } else {
                                            player.sendMessage(ChatColor.GREEN + String.format("%b has been set for toggle inventory!", false));
                                            arena.setToggleInventory(false);
                                        }
                                        // player.sendMessage(ChatColor.RED + "This is not boolean!");
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] invtoggle [boolean, true-false]");
                                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Inventory toggle: if true, the player receives the inventory they had on them at the moment of connection.");
                                    }
                                    break;
                                case "combomode":
                                    if (args.length == 3) {
                                        String tcomboBool = args[2];
                                        if (Boolean.parseBoolean(tcomboBool)) {
                                            player.sendMessage(ChatColor.GREEN + String.format("%b has been set for combo mode!", true));
                                            arena.setComboMode(true);
                                        } else {
                                            player.sendMessage(ChatColor.GREEN + String.format("%b has been set for combo mode!", false));
                                            arena.setComboMode(false);
                                        }
                                        // player.sendMessage(ChatColor.RED + "This is not boolean!");
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_PURPLE + "/event editarena [name] combomode [boolean, true-false]");
                                        player.sendMessage(ChatColor.LIGHT_PURPLE + "If you set this value to true, the combo mode will be enabled. Conversely, if set to false, it will be disabled.");
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
    public Map<String, User> getTop10Wins() {
        return userManager.getToplist(10).stream().collect(Collectors.toMap(User::getName, Function.identity()));
    }

    @Override
    public void disable() {
        if (isActiveMatch()) {
            mainMatch.onDestroy();
        }
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
        kitManager.getSerializer().getStorage().reload();
        userManager.getSerializer().getStorage().reload();
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

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

}
