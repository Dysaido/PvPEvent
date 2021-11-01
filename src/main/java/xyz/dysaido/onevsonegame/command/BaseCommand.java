package xyz.dysaido.onevsonegame.command;

import org.bukkit.command.Command;

public abstract class BaseCommand extends Command {

    public BaseCommand(String name) {
        super(name);
        if (getClass().isAnnotationPresent(SubCommand.class)) {
            SubCommand subCommand = getClass().getAnnotation(SubCommand.class);

        }
    }

}
