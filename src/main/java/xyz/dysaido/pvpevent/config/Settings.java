package xyz.dysaido.pvpevent.config;

import xyz.dysaido.pvpevent.api.config.Config;
import xyz.dysaido.pvpevent.util.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Settings extends Config {

    @Ignore
    public static final Settings IMP = new Settings();

    @Comment({
            "PvPEvent - This is a 1v1 game plugin for your server. You can create arenas where players can engage in 1v1 matches.",
            "@Discord: dysaido",
            "@Github: https://github.com/Dysaido/PvPEvent"
    })
    @Final
    public String VERSION = "1.2.3";
    @Comment("If you want to see some information from this plugin, turn on this!")
    public boolean DEBUG = false;
    @Create
    public PERMISSION PERMISSION;
    @Create
    public COMMAND COMMAND;
    @Create 
    public MESSAGE MESSAGE;
    @Create
    public COUNTDOWN COUNTDOWN;
    @Create
    public GUI GUI;

    public void reload(File file) {
        Logger.information("Settings", "Reload all field from settings configuration class and load from parent file");
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
        public String BASE_JOIN_CANNOT = "&cYou can't join! (Maybe the event has reached max capacity)";

        public String BASE_LEAVE_BROADCAST = "&c{user} &7left the battelfield while the battle was going on!";
        public String BASE_LEAVE_SUCCESS = "&cYou leaved the event!";
        public String BASE_LEAVE_CANNOT = "&cYou aren't on the queue!";

        public String BASE_SPECTATE_SUCCESS = "&aYou joined the event as a spectator!";
        public String BASE_SPECTATE_JOINED = "&cYou are already on the queue";

        public String COUNTDOWN_QUEUE = "&7PvPEvent will be started &b{second}&7s. Present: &r{present} &7- Arena: &r{arena} &7- Kit: &r{kit}";
        public String COUNTDOWN_START = "&6Event starts: {second}s";
    }

    @Comment({
            "Event's permissions"
    })
    public static class PERMISSION {
        @Final
        @Comment({
                "Enables command execution during 1v1 matches.",
                "Not modifiable!"
        })
        public String COMMAND_PERFORM = "event.command.perform";

        @Final
        @Comment({
                "Enables all commands available in the plugin.",
                "Not modifiable!"
        })
        public String COMMAND_ADMIN = "event.command.admin";

        @Final
        @Comment({
                "Enables command execution for default players.",
                "/event join/leave/spectate.",
                "Not modifiable!"
        })
        public String COMMAND_DEFAULT = "event.command.default";

        @Final
        @Comment({
                "Enables listing of admin commands.",
                "Not modifiable!"
        })
        public String COMMAND_HELP = "event.command.help";

        @Final
        @Comment({
                "Enables starting a PvP event.",
                "Not modifiable!"
        })
        public String COMMAND_HOST = "event.command.host";

        @Final
        @Comment({
                "Enables creating and modifying arenas.",
                "When you modify an arena, it's important to remember to save the changes afterward using /event editarena [name] save, as it will not save to the config file otherwise!",
                "Not modifiable!"
        })
        public String COMMAND_ARENA = "event.command.arena";

        @Final
        @Comment({
                "Enables creating and modifying kits.",
                "Not modifiable!"
        })
        public String COMMAND_KIT = "event.command.kit";

        @Final
        @Comment({
                "Enables stopping a running event.",
                "Not modifiable!"
        })
        public String COMMAND_STOP = "event.command.stop";

        @Final
        @Comment({
                "Enables reloading the plugin.",
                "Not modifiable!"
        })
        public String COMMAND_RELOAD = "event.command.reload";

        @Final
        @Comment({
                "Enables the plugin's punishment handling system.",
                "Not modifiable!"
        })
        public String COMMAND_PUNISHMENT = "event.command.punishment";
    }

    @Comment("Command settings")
    public static class COMMAND {

        public String DEFAULT_NO_EVENT = "&cThere isn't any event!";

        @Comment("Help for default users")
        public String EVENT_TITLE = "&6&lEvent Commands";
        public String EVENT_JOIN = "&e/event join";
        public String EVENT_LEAVE = "&e/event leave";
        public String EVENT_SPECTATE = "&e/event spectate";
        public String EVENT_TOPLIST = "&e/event toplist";
        public String EVENT_HELP = "&e/event help - admin commands";
    }

    @Comment({
            "Countdown settings",
            "!!!IMPORTANT!!!",
            "\tIf the QueueTimer value is less than 10, it will be set to 10, as what kind of event would it be if you had no time to join?",
            "\tIf the FightTimer value is less than 3, it will immediately start the next round! This can be used to control how long players are frozen."
    })
    public static class COUNTDOWN {
        @Comment({
                "This dictates when the intermediary text will be shown.",
        })
        public List<Integer> BASE_CREATE_ANNOUNCE = Arrays.asList(50, 30, 20, 15, 10, 5, 4, 3, 2, 1);
        @Comment({
                "This controls the countdown to the start.",
                "If it's less than three, the PvP event starts immediately."
        })
        public int BASE_START_TIMES = 3;
        @Comment({
                "This dictates when the intermediary text will be shown.",
        })
        public List<Integer> BASE_START_ANNOUNCE = Arrays.asList(5, 4, 3, 2, 1);
        @Comment({
                "This dictates when the intermediary text will be shown.",
        })
        public List<Integer> BASE_NEXTROUND_ANNOUNCE = Arrays.asList(5, 4, 3, 2, 1);
    }

    @Comment({
            "GUI settings"
    })
    public static class GUI {
        @Comment({
                "This setting indicates the position of the leave button. Its value can range between 0 and 8."
        })
        public int QUEUE_LEAVE_SLOTBAR = 8;
        @Comment({
                "This setting specifies the name of the leave button."
        })
        public String QUEUE_LEAVE_NAME = "&aLEAVE &7(Click to leave)";
    }

}
