package xyz.dysaido.onevsonegame.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.util.Format;

import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand<P extends JavaPlugin> extends Command {

    protected final P plugin;
    private String permission = "";
    private boolean onlyPlayer = false;
    private boolean onlyConsole = false;

    public BaseCommand(P plugin, String name, String description, String usage, List<String> aliases) {
        super(name, Format.colored(description), Format.colored(usage), aliases);
        this.plugin = plugin;
    }

    public BaseCommand(P plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @SafeVarargs
    public final void child(BaseCommand<P>... baseCommand) {

    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        List<String> list = Arrays.asList(strings);
        if (commandSender instanceof Player) {
            if (onlyConsole) {
                commandSender.sendMessage(MESSAGE.ONLY_CONSOLE.format());
            } else {
                Player player = (Player) commandSender;
                if (permission != null && !permission.isEmpty()) {
                    if (!player.hasPermission(permission)) {
                        commandSender.sendMessage(MESSAGE.NO_PERMISSION.format());
                        return true;
                    }
                }
                execute(player, s, list);
            }
        } else {
            if (onlyPlayer) {
                commandSender.sendMessage(MESSAGE.ONLY_PLAYER.format());
            } else {
                ConsoleCommandSender console = (ConsoleCommandSender) commandSender;
                execute(console, s, list);
            }
        }
        return true;
    }

    public abstract void execute(ConsoleCommandSender console, String label, List<String> list);

    public abstract void execute(Player player, String label, List<String> list);

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    public void setOnlyPlayer(boolean onlyPlayer) {
        this.onlyPlayer = onlyPlayer;
    }

    public boolean isOnlyConsole() {
        return onlyConsole;
    }

    public void setOnlyConsole(boolean onlyConsole) {
        this.onlyConsole = onlyConsole;
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
