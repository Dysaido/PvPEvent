package xyz.dysaido.onevsonegame.command.impl;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.command.BaseCommand;
import xyz.dysaido.onevsonegame.ring.RingCache;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.Collections;
import java.util.List;

public class EventCommand extends BaseCommand<OneVSOneGame> {

    private RingCache ringCache;

    public EventCommand(OneVSOneGame plugin) {
        super(plugin, "event", "Event command", "/event [options]", Collections.emptyList());
        setOnlyPlayer(true);
    }

    @Override
    public void execute(ConsoleCommandSender console, String label, List<String> list) {

    }

    @Override
    public void execute(Player player, String label, List<String> list) {
        if (player.hasPermission("event.command.perform")) {
            if (list.size() == 0) {
                sendHelp(player);
                return;
            }
            switch (list.get(0)) {
                case "create":
                    if (list.size() > 1) {
                        ringCache = new RingCache(list.get(1));
                        player.sendMessage("Create");
                    } else {
                        sendHelp(player);
                    }
                    break;
                case "lobby":
                    if (ringCache == null) {
                        player.sendMessage("Create event, please");
                    } else {
                        ringCache.setLobby(player.getLocation());
                        player.sendMessage("Lobby");
                    }
                    break;
                case "spawn":
                    if (ringCache == null) {
                        player.sendMessage("Create event, please");
                    } else {
                        ringCache.setSpawn(player.getLocation());
                        player.sendMessage("Spawn");
                    }
                    break;
                case "spawn1":
                    if (ringCache == null) {
                        player.sendMessage("Create event, please");
                    } else {
                        ringCache.setSpawn1(player.getLocation());
                        player.sendMessage("Spawn1");
                    }
                    break;
                case "spawn2":
                    if (ringCache == null) {
                        player.sendMessage("Create event, please");
                    } else {
                        ringCache.setSpawn2(player.getLocation());
                        player.sendMessage("Spawn2");
                    }
                    break;
                case "inventory":
                    if (ringCache == null) {
                        player.sendMessage("Create event, please");
                    } else {
                        PlayerInventory inventory = player.getInventory();
                        ringCache.setContents(inventory.getContents());
                        ringCache.setArmor(inventory.getArmorContents());
                        player.sendMessage("Inventory");
                    }
                    break;
                case "save":
                    if (ringCache == null) {
                        player.sendMessage("Create event, please");
                    } else {
                        plugin.getRingManager().save(ringCache);
                        ringCache = null;
                        player.sendMessage("Save");
                    }
                    break;
                case "host":
                    if (list.size() > 1) {
                        if (plugin.getRingManager().get(list.get(1)) != null) {
                            plugin.getMatchManager().create(plugin.getRingManager().get(list.get(1)));
                            player.sendMessage("Host");
                        } else {
                            player.sendMessage("Couldn't find this event");
                        }
                    } else {
                        sendHelp(player);
                    }
                    break;
                default:
                    sendHelp(player);
                    break;
            }
        }
        switch (list.get(0)) {
            case "join":
                plugin.getMatchManager().getMatch().ifPresent(match -> match.join(player));
                break;
            case "leave":
                plugin.getMatchManager().getMatch().ifPresent(match -> match.leave(player));
                break;
        }
    }

    private final void sendHelp(Player player) {
        player.sendMessage(Format.colored("&d/event create eventname &7- Type event name, for example : /event create test"));
        player.sendMessage(Format.colored("&d/event lobby &7- First create event and set event's lobby (your location, spectator place)"));
        player.sendMessage(Format.colored("&d/event spawn &7- First create event and set event's spawn (your location, final spawn)"));
        player.sendMessage(Format.colored("&d/event spawn1 &7- First create event and set event's spawn1 (your location, player1's spawn)"));
        player.sendMessage(Format.colored("&d/event spawn2 &7- First create event and set event's spawn2 (your location, player2's spawn)"));
        player.sendMessage(Format.colored("&d/event inventory &7- First create event and set event's inventory (your inventory, event player's inventory)"));
        player.sendMessage(Format.colored("&d/event save &7- First create event and set some settings then save it"));
        player.sendMessage(Format.colored("&d/event host eventname &7- Start event"));
    }

}
