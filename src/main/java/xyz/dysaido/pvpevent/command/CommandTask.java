package xyz.dysaido.pvpevent.command;

import org.bukkit.command.CommandSender;

public interface CommandTask<S extends CommandSender> {

    boolean apply(S sender, String[] args);
}
