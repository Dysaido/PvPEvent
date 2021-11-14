package xyz.dysaido.onevsonegame.command.impl;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.command.BaseCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventCommand extends BaseCommand<OneVSOneGame> {

    public EventCommand(OneVSOneGame plugin) {
        super(plugin, "event", "Event command", "/event [options]", Collections.emptyList());
        setPermission("event.command.perform");
    }

    @Override
    public void execute(ConsoleCommandSender console, String label, List<String> list) {

    }

    @Override
    public void execute(Player player, String label, List<String> list) {

    }
}
