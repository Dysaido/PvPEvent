package xyz.dysaido.pvpevent.command;

import org.bukkit.command.CommandSender;
import xyz.dysaido.pvpevent.util.Logger;

public class SubCommand<S extends CommandSender> {

    private final String name;
    private CommandTask<S> command = (sender, args) -> {};
    private String alias = "";
    private String perm = "";

    public SubCommand(String name) {
        this.name = name;
    }

    public SubCommand<S> setCommand(CommandTask<S> task) {
        this.command = task;
        return this;
    }

    void execute(S sender, String[] args) {
        if (command != null) {
            Logger.debug(name, String.format("perm: %s, alias: %s, executing...", perm, alias));
            command.apply(sender, args);
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
        return name;
    }

    public String getPerm() {
        return perm;
    }

    public SubCommand<S> setPerm(String perm) {
        this.perm = perm;
        return this;
    }
}
