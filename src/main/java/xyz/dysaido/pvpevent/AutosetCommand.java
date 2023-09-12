package xyz.dysaido.pvpevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.dysaido.pvpevent.command.CommandTask;
import xyz.dysaido.pvpevent.model.AutoRun;
import xyz.dysaido.pvpevent.model.manager.AutosetManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class AutosetCommand implements CommandTask<CommandSender> {

    private final PvPEventPlugin pvpEvent;
    private final AutosetManager manager;
    public AutosetCommand(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.manager = pvpEvent.getAutosetManager();
    }
    @Override
    public boolean apply(CommandSender sender, String[] args) {
        /*String name = args[0];
        String arenaName = args[1];
        String dateC = args[2];

        String command = "eco give {sender} 10000";
        String broadcast = "EDIT CONFIG (autosets.yml)";

        manager.withAction(name).then(run -> {
            run.setArenaName(arenaName);
            run.setCommand(command);
            run.setBroadcast(broadcast);
            run.setDate(parseTime(dateC).getTime());
            manager.getSerializer().append(run);
        });*/
        sender.sendMessage(ChatColor.GOLD + "BETA");
        return false;
    }

    private Date parseTime(String inputTime) {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat ampmFormat = new SimpleDateFormat("h:mm a");

        try {
            return hourFormat.parse(inputTime);
        } catch (ParseException e1) {
            try {
                return ampmFormat.parse(inputTime);
            } catch (ParseException e2) {
                e1.addSuppressed(e2);
                Bukkit.getLogger().log(Level.WARNING, "Error:", e1);
                return null;
            }
        }
    }
}
