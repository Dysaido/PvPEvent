package xyz.dysaido.onevsonegame.setting;

public class Settings {

    @Config.Options(path = "message", name = "join")
    public static String JOIN_MESSAGE = "&b{player} &7joined the event!";

    @Config.Options(path = "message", name = "leave")
    public static String LEAVE_MESSAGE = "&b{player} &7left the event!";

    @Config.Options(path = "message", name = "already_joined")
    public static String ALREADY_JOINED_MESSAGE = "&7You are already joined!";

    @Config.Options(path = "message", name = "event_not_available")
    public static String EVENT_NOT_AVAILABLE_MESSAGE = "&cEvent not available!";

    @Config.Options(path = "message", name = "nextround")
    public static String NEXT_ROUND = "&bRound {round}";

    @Config.Options(path = "message", name = "waiting")
    public static String WAITING_MESSAGE = "&7Join for event &b{second} sec";

    @Config.Options(path = "message", name = "clickable_broadcast")
    public static String CLICKABLE_MESSAGE = "&7(Click to join)";

    @Config.Options(path = "message", name = "join_finished")
    public static String EVENT_JOIN_FINISHED_MESSAGE = "&bEvent will start";

    @Config.Options(path = "message", name = "starting")
    public static String EVENT_WILL_START_MESSAGE = "&7Event will be started &b{second} &7sec";

    @Config.Options(path = "message", name = "start")
    public static String EVENT_START_MESSAGE = "&bEvent start";

    @Config.Options(path = "message", name = "winner")
    public static String EVENT_WINNER_MESSAGE = "&7Event's winner &b{player}";

    @Config.Options(path = "message", name = "ending")
    public static String EVENT_ENDING_MESSAGE = "&7Event will be ended &b{second} &7sec";

    @Config.Options(path = "message", name = "end")
    public static String EVENT_ENDED_MESSAGE = "&bEvent ended!";

    @Config.Options(path = "command.message", name = "create")
    public static String COMMAND_CREATE_MESSAGE = "&fType event name";

    @Config.Options(path = "command.message", name = "host")
    public static String COMMAND_HOST_MESSAGE = "&fStart event";

    @Config.Options(path = "command.message", name = "inventory")
    public static String COMMAND_INVENTORY_MESSAGE = "&fFirst create event and set event's inventory (your inventory, event player's inventory)";

    @Config.Options(path = "command.message", name = "join")
    public static String COMMAND_JOIN_MESSAGE = "&fType for join";

    @Config.Options(path = "command.message", name = "leave")
    public static String COMMAND_LEAVE_MESSAGE = "&fType for leave";

    @Config.Options(path = "command.message", name = "lobby")
    public static String COMMAND_LOBBY_MESSAGE = "&fFirst create event and set event's lobby (your location, spectator place)";

    @Config.Options(path = "command.message", name = "save")
    public static String COMMAND_SAVE_MESSAGE = "&fFirst create event and save settings";

    @Config.Options(path = "command.message", name = "spawn")
    public static String COMMAND_SPAWN_MESSAGE = "&fFirst create event and set event's spawn (your location, final spawn)";

    @Config.Options(path = "command.message", name = "spawn1")
    public static String COMMAND_SPAWN1_MESSAGE = "&fFirst create event and set event's spawn1 (your location, player1's spawn)";

    @Config.Options(path = "command.message", name = "spawn2")
    public static String COMMAND_SPAWN2_MESSAGE = "&fFirst create event and set event's spawn2 (your location, player2's spawn)";

    @Config.Options(path = "command.message", name = "reload")
    public static String COMMAND_RELOAD_MESSAGE = "&fReload this configs and some options";

    @Config.Options(path = "command.message", name = "reload")
    public static String COMMAND_MENU_MESSAGE = "&fYou perform this command for open menu";

    @Config.Options(path = "settings.count", name = "waiting")
    public static int WAITING = 30;

    @Config.Options(path = "settings.count", name = "starting")
    public static int STARTING = 5;

    @Config.Options(path = "settings.count", name = "ending")
    public static int ENDING = 5;

    @Config.Options(path = "settings.bool", name = "debug")
    public static boolean DEBUG = false;

    @Config.Options(path = "settings.bool", name = "freeze")
    public static boolean FREEZE = false;

}
