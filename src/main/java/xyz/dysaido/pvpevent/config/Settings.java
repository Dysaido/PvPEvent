package xyz.dysaido.pvpevent.config;

import xyz.dysaido.pvpevent.api.config.Config;
import xyz.dysaido.pvpevent.util.Logger;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Settings extends Config {

    @Ignore
    public static final Settings IMP = new Settings();

    @Comment({
            "PvPEvent - This is an 1v1 game plugin for your server. You can make some arena and in those the players play an 1v1 game",
            "@Author: Dysaido",
            "@Discord: dysaido",
            "@Github: https://github.com/Dysaido",
            "meow :3"
    })
    @Final
    public String VERSION = "1.2.0-pre3";
    @Comment("If you want to see some information from this plugin, turn on this!")
    public boolean DEBUG = false;
    @Create
    public PERMISSION PERMISSION;
    @Create
    public COMMAND COMMAND;
    @Create 
    public MESSAGE MESSAGE;

    public void reload(File file) {
        Logger.debug("Settings", "Reload all field from settings configuration class and load from parent file");
        load(file);
        save(file);
    }
    
    @Comment({
            "Event's separated messages"
    })
    public static class MESSAGE {

        public String MATCH_HOVER_TEXT = "&7(Click to join)";
        public String MATCH_CLICK_TEXT = "/pvpevent:event join";
        public String MATCH_DEATH_TEXT = "&b{victim} &7was killed by &b{winner}&7!";

        public String MATCH_STOP_TEXT = "&c&lPvPEvent has been stopped by {executor}!";

        public String SUMO_NEXTROUND_COUNTDOWN_TEXT = "&7PvP: &e{second}&7s";
        public String SUMO_NEXTROUND_SUCCESS_TEXT = "&7Round: &e{round} &7- &e{user1} &7vs &e{user2}";
        public String SUMO_NEXTROUND_WITH_WINNER_TEXT = "&b{user} &7has won this event! Present: &e{present}";
        public String SUMO_NEXTROUND_WITHOUT_WINNER_TEXT = "&7There is not winner. Because of this the {present} hasn't given to winner";
        public String BASE_JOIN_SUCCESS = "&aYou joined the event!";
        public String BASE_JOIN_JOINED = "&cYou are already on the queue";
        public String BASE_JOIN_CANNOT = "&cYou can't join!";

        public String BASE_LEAVE_BROADCAST = "&c{user} &7left the battelfield while the battle was going on!";
        public String BASE_LEAVE_SUCCESS = "&cYou leaved the event!";
        public String BASE_LEAVE_CANNOT = "&cYou aren't on the queue!";

        public String BASE_SPECTATE_SUCCESS = "&aYou joined the event as a spectator!";
        public String BASE_SPECTATE_JOINED = "&cYou are already on the queue";

        public String COUNTDOWN_QUEUE = "&7PvPEvent will be started &b{second}&7s. Present: &r{present} &7- Arena: &r{arena} &7- Kit: &r{kit}";
        public String COUNTDOWN_START = "&6Event starts: {second}s";
    }

    @Comment({
            "Event's permissions",
            "Admin permission event.command.admin",
            "Executing the command during the event: event.command.perform"
    })
    public static class PERMISSION {
        public String COMMAND_DEFAULT = "event.command.default";
        public String COMMAND_HELP = "event.command.help";
        public String COMMAND_HOST = "event.command.host";
        public String COMMAND_ARENA = "event.command.arena";
        public String COMMAND_KIT = "event.command.kit";
        public String COMMAND_STOP = "event.command.stop";
        public String COMMAND_RELOAD = "event.command.reload";
        public String COMMAND_PUNISHMENT = "event.command.punishment";
    }

    @Comment("Command settings")
    public static class COMMAND {

        public String DEFAULT_NO_EVENT = "&cThere isn't any event!";
        @Comment("Help for default users")
        public List<String> MSG_HELP = Collections.singletonList("\n" +
                "&6&lEvent Commands\n" +
                "&e/event join - type to join\n" +
                "&e/event leave - type to leave\n" +
                "&e/event spectate - type to spectate\n" +
                "&e/event view (gui)\n" +
                "&e/event help - admin commands\n"
        );

        @Comment("Help for member of staff team")
        public List<String> MSG_HELP_ADMIN = Collections.singletonList("\n" +
                "&5&lAdmin Commands\n" +
                "&d/event autoset <arena> <bc> <command> <schedule> \n" +
                "&d/event host <arena> <bc-present-text>\n" +
                "&d/event createarena <name>\n" +
                "&d/event createkit <name>\n" +
                "&d/event view (gui)\n" +
                "&d/event edit-kit <name> setinventory (who executes the command, his inventory will be saved)\n" +
                "&d/event edit-arena <name> <save|setlobby|setpos1|setpos2|setkit|setcapacity|setqueuecountdown(second)|setfightcountdown(second)>\n" +
                "&d/event delete-arena <name>\n" +
                "&d/event stop\n" +
                "&d/event reload - reload this configs and some options\n" +
                "&d/event kick <user> <reason>\n" +
                "&d/event ban <user> <reason>\n" +
                "&d/event unban <user>\n"
        );
    }
}
