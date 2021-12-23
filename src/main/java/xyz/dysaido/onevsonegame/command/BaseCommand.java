package xyz.dysaido.onevsonegame.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;
import xyz.dysaido.onevsonegame.OneVSOneGame;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseCommand extends Command {

    protected final OneVSOneGame plugin;

    public BaseCommand(OneVSOneGame plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    public BaseCommand(OneVSOneGame plugin, String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
    }

    private static boolean isCommandSender(Permissible permissible) {
        return permissible instanceof CommandSender;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            sender.sendMessage(Format.colored(getPermissionMessage()));
            return false;
        }
        this.handle(sender, label, args);
        return false;
    }

    public abstract void handle(CommandSender sender, String label, String[] args);

    protected void sendStaffMessage(String message) {
        Set<Permissible> subscriptions = Bukkit.getPluginManager().getPermissionSubscriptions("event.broadcast.admin");
        subscriptions.stream().filter(BaseCommand::isCommandSender).map(CommandSender.class::cast).forEach(sender -> sender.sendMessage(message));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Player player = sender instanceof Player ? (Player) sender : null;
        ArrayList<String> list = new ArrayList<>();
        for (Player player1 : sender.getServer().getOnlinePlayers()) {
            String name = player1.getName();
            if (player == null || player.canSee(player1)) {
                if (args.length != 0) {
                    if (StringUtil.startsWithIgnoreCase(name, args[args.length - 1])) {
                        list.add(name);
                    }
                }
            }
        }
        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    public enum MESSAGE {
        NO_PERMISSION("&cYou don't have permission to perform this command!"),
        ONLY_CONSOLE("&cVanilla entity don't authorized to perform this command!"),
        ONLY_PLAYER("&cConsole don't authorized to perform this command, because you are not an entity!");

        private final String msg;

        MESSAGE(String msg) {
            this.msg = msg;
        }

        public String format() {
            return ChatColor.translateAlternateColorCodes('&', msg);
        }

    }
}
