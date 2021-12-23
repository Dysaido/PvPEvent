package xyz.dysaido.onevsonegame.command.impl;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.command.BaseCommand;
import xyz.dysaido.onevsonegame.match.BaseMatch;
import xyz.dysaido.onevsonegame.setting.Settings;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.*;

public class EventCommand extends BaseCommand {

    private final List<String> arguments = Arrays.asList("join", "leave");

    public EventCommand(OneVSOneGame plugin) {
        super(plugin, "event");
    }

    @Override
    public void handle(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "join":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Optional<BaseMatch> optionalMatch = plugin.getMatchHandler().getMatch();
                        if (optionalMatch.isPresent()) {
                            BaseMatch match = optionalMatch.get();
                            match.join(player);
                        } else {
                            player.sendMessage(Format.colored(Settings.EVENT_NOT_AVAILABLE_MESSAGE));
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
                    }
                    break;
                case "leave":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Optional<BaseMatch> optionalMatch = plugin.getMatchHandler().getMatch();
                        if (optionalMatch.isPresent()) {
                            BaseMatch match = optionalMatch.get();
                            match.leave(player);
                        } else {
                            player.sendMessage(Format.colored(Settings.EVENT_NOT_AVAILABLE_MESSAGE));
                        }
                    } else {
                        sender.sendMessage(MESSAGE.ONLY_PLAYER.format());
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
        Settings.COMMAND_HELP_EVENT.stream().map(Format::colored).forEach(sender::sendMessage);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>(arguments.size()));
        } else {
            return (args.length == 2 ? super.tabComplete(sender, alias, args) : ImmutableList.of());
        }
    }
}
