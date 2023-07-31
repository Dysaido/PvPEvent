package xyz.dysaido.pvpevent.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.util.CommandMapUtil;
import xyz.dysaido.pvpevent.util.Logger;

import java.util.Map;

public class ParentCommand extends AbstractCommand {
    private static final String TAG = "ParentCommand";
    public ParentCommand(PvPEventPlugin pvpEvent, String name) {
        super(pvpEvent, name);
    }

    public void load() {
        Logger.debug(TAG, String.format("Load - name: %s, perm: %s", getName(), getPermission()));
        CommandMapUtil.getCommandMap(Bukkit.getServer()).register("pvpevent", this);
    }

    public void unload() {
        Logger.debug(TAG, String.format("Unload - name: %s, perm: %s", getName(), getPermission()));
        SimpleCommandMap commandMap = CommandMapUtil.getCommandMap(Bukkit.getServer());
        Map<String, Command> knownCommands = CommandMapUtil.getKnownCommands(commandMap);
        knownCommands.remove(getName()).unregister(commandMap);
        this.subcommands.clear();
    }
}
