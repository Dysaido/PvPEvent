package xyz.dysaido.onevsonegame.command.impl;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.command.BaseCommand;
import xyz.dysaido.onevsonegame.ring.RingCache;
import xyz.dysaido.onevsonegame.util.ItemSerializer;

import java.util.Collections;
import java.util.List;

public class EventCommand extends BaseCommand<OneVSOneGame> {

    private RingCache ringCache;

    public EventCommand(OneVSOneGame plugin) {
        super(plugin, "event", "Event command", "/event [options]", Collections.emptyList());
//        setPermission("event.command.perform");
        setOnlyPlayer(true);
    }

    @Override
    public void execute(ConsoleCommandSender console, String label, List<String> list) {

    }

    @Override
    public void execute(Player player, String label, List<String> list) {
        if (player.hasPermission("event.command.perform")) {
            switch (list.get(0)) {
                case "create":
                    ringCache = new RingCache(list.get(1));
                    player.sendMessage("Create");
                    break;
                case "lobby":
                    ringCache.setLobby(player.getLocation());
                    player.sendMessage("Lobby");
                    break;
                case "spawn":
                    ringCache.setSpawn(player.getLocation());
                    player.sendMessage("Spawn");
                    break;
                case "spawn1":
                    ringCache.setSpawn1(player.getLocation());
                    player.sendMessage("Spawn1");
                    break;
                case "spawn2":
                    ringCache.setSpawn2(player.getLocation());
                    player.sendMessage("Spawn2");
                    break;
                case "inventory":
                    PlayerInventory inventory = player.getInventory();
                    ringCache.setContents(ItemSerializer.serialize(inventory.getContents()));
                    ringCache.setArmor(ItemSerializer.serialize(inventory.getArmorContents()));
                    player.sendMessage("Inventory");
                    break;
                case "save":
                    plugin.getRingManager().save(ringCache);
                    player.sendMessage("Save");
                    break;
                case "host":
                    plugin.getRingManager().load();
                    plugin.getMatchManager().create(plugin.getRingManager().get(list.get(1)));
                    player.sendMessage("Host");
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
}
