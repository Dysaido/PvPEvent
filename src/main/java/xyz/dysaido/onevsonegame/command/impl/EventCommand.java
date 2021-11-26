package xyz.dysaido.onevsonegame.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.command.BaseCommand;
import xyz.dysaido.onevsonegame.command.SubCommand;
import xyz.dysaido.onevsonegame.ring.RingCache;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.Collections;

public class EventCommand extends BaseCommand<OneVSOneGame> {

    private static RingCache ringCache;

    public EventCommand(OneVSOneGame plugin) {
        super(plugin, "event", "Event command", "/event [options]", Collections.emptyList());
        setSeeHelpPermission("event.command.help");
    }

    @SubCommand(name = "menu", usage = "/event menu", onlyPlayer = true)
    public static class Menu extends SubAdapter {
        public Menu() {
            super();
            setDescription(Settings.COMMAND_MENU_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            plugin.getMenuManager().getMainMenu().open(player);
        }
    }

    @SubCommand(name = "reload", usage = "/event reload", permission = "event.command.perform")
    public static class Reload extends SubAdapter {
        public Reload() {
            super();
            setDescription(Settings.COMMAND_RELOAD_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            plugin.reloadConfig();
            sender.sendMessage(Format.colored("&aSuccess reload!"));
        }
    }

    @SubCommand(name = "join", usage = "/event join", onlyPlayer = true)
    public static class Join extends SubAdapter {
        public Join() {
            super();
            setDescription(Settings.COMMAND_JOIN_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            plugin.getMatchManager().getMatch().ifPresent(match -> match.join(player));
        }
    }

    @SubCommand(name = "leave", usage = "/event leave", onlyPlayer = true)
    public static class Leave extends SubAdapter {

        public Leave() {
            super();
            setDescription(Settings.COMMAND_LEAVE_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            plugin.getMatchManager().getMatch().ifPresent(match -> match.leave(player));
        }
    }

    @SubCommand(name = "create", usage = "/event create [name]", onlyPlayer = true, permission = "event.command.perform")
    public static class Create extends SubAdapter {

        public Create() {
            super();
            setDescription(Settings.COMMAND_CREATE_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (args.length > 0) {
                ringCache = new RingCache(args[0]);
                player.sendMessage("Create");
            } else {
                sendHelp(this, sender);
            }
        }
    }

    @SubCommand(name = "lobby", usage = "/event lobby", onlyPlayer = true, permission = "event.command.perform")
    public static class Lobby extends SubAdapter {

        public Lobby() {
            super();
            setDescription(Settings.COMMAND_LOBBY_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (ringCache == null) {
                player.sendMessage("Create event, please");
            } else {
                ringCache.setLobby(player.getLocation());
                player.sendMessage("Lobby");
            }
        }
    }

    @SubCommand(name = "spawn", usage = "/event spawn", onlyPlayer = true, permission = "event.command.perform")
    public static class Spawn extends SubAdapter {

        public Spawn() {
            super();
            setDescription(Settings.COMMAND_SPAWN_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (ringCache == null) {
                player.sendMessage("Create event, please");
            } else {
                ringCache.setSpawn(player.getLocation());
                player.sendMessage("Spawn");
            }
        }
    }

    @SubCommand(name = "spawn1", usage = "/event spawn1", onlyPlayer = true, permission = "event.command.perform")
    public static class Spawn1 extends SubAdapter {

        public Spawn1() {
            super();
            setDescription(Settings.COMMAND_SPAWN1_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (ringCache == null) {
                player.sendMessage("Create event, please");
            } else {
                ringCache.setSpawn1(player.getLocation());
                player.sendMessage("Spawn1");
            }
        }
    }

    @SubCommand(name = "spawn2", usage = "/event spawn2", onlyPlayer = true, permission = "event.command.perform")
    public static class Spawn2 extends SubAdapter {

        public Spawn2() {
            super();
            setDescription(Settings.COMMAND_SPAWN2_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (ringCache == null) {
                player.sendMessage("Create event, please");
            } else {
                ringCache.setSpawn2(player.getLocation());
                player.sendMessage("Spawn2");
            }
        }
    }

    @SubCommand(name = "inventory", usage = "/event inventory", onlyPlayer = true, permission = "event.command.perform")
    public static class Inventory extends SubAdapter {

        public Inventory() {
            super();
            setDescription(Settings.COMMAND_INVENTORY_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (ringCache == null) {
                player.sendMessage("Create event, please");
            } else {
                PlayerInventory inventory = player.getInventory();
                ringCache.setContents(inventory.getContents());
                ringCache.setArmor(inventory.getArmorContents());
                player.sendMessage("Inventory");
            }
        }
    }

    @SubCommand(name = "save", usage = "/event save", onlyPlayer = true, permission = "event.command.perform")
    public static class Save extends SubAdapter {

        public Save() {
            super();
            setDescription(Settings.COMMAND_SAVE_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (ringCache == null) {
                player.sendMessage("Create event, please");
            } else {
                plugin.getRingManager().save(ringCache);
                ringCache = null;
                player.sendMessage("Save");
            }
        }
    }

    @SubCommand(name = "host", usage = "/event host [name]", permission = "event.command.perform")
    public static class Host extends SubAdapter {

        public Host() {
            super();
            setDescription(Settings.COMMAND_HOST_MESSAGE);
        }

        @Override
        protected void execute(CommandSender sender, String[] args) {
            if (args.length > 0) {
                String name = args[0];
                if (plugin.getRingManager().get(name) != null) {
                    plugin.getMatchManager().createSolo(plugin.getRingManager().get(name));
                    sender.sendMessage("Host");
                } else {
                    sender.sendMessage("Couldn't find this event");
                }
            } else {
                sendHelp(this, sender);
            }
        }
    }

}
