package xyz.dysaido.pvpevent.command;

import org.bukkit.command.CommandSender;
import xyz.dysaido.pvpevent.util.BukkitHelper;
import xyz.dysaido.pvpevent.util.Logger;

public class SubCommand<S extends CommandSender> {

    private final CommandInfo info;
    private CommandTask<S> command = (sender, args) -> true;
    private String alias = "";
    private String perm = "";

    public SubCommand(CommandInfo info) {
        this.info = info;
    }

    public SubCommand<S> setCommand(CommandTask<S> task) {
        this.command = task;
        return this;
    }

    void execute(S sender, String[] args) {
        if (command != null) {
            Logger.debug(info.getName(), String.format("perm: %s, alias: %s, executing...", perm, alias));
            if (!command.apply(sender, args)) {
                sender.sendMessage(BukkitHelper.colorize(String.format("%s &5- %s", info.getUsage(), info.getDescription())));
            }
        }
    }

    public SubCommand<S> setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return info.getName();
    }

    public String getPerm() {
        return perm;
    }

    public SubCommand<S> setPerm(String perm) {
        this.perm = perm;
        return this;
    }
}
