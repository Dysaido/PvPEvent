package xyz.dysaido.onevsonegame.setting;

import java.util.Collections;
import java.util.List;

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
    public static String NEXT_ROUND = "&bRound {round} &7: &b{player1} &7vs &b{player2}";

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

    @Config.Options(path = "settings.bool", name = "inventory-freeze")
    public static boolean INVENTORY_FREEZE = false;

    @Config.Options(path = "command.help", name = "event")
    public static List<String> COMMAND_HELP_EVENT = Collections.singletonList("\n" +
            "&6&lEvent Commands\n" +
            "&e/event join - type to join an available event\n" +
            "&e/event leave - type to leave an available event\n"
    );

    @Config.Options(path = "command.help", name = "events")
    public static List<String> COMMAND_HELP_EVENTS = Collections.singletonList("\n" +
            "&5&lEvents Commands\n" +
            "&d/events create - type event name\n" +
            "&d/events delete - type to delete an event\n" +
            "&d/events host - start event\n" +
            "&d/events stop - you perform this command to stop event\n" +
            "&d/events list - you perform this command to list saved events\n" +
            "&d/events lobby - first create event and set event lobby\n" +
            "&d/events spawn - first create event and set event spawn\n" +
            "&d/events spawn1 - first create event and set event spawn1\n" +
            "&d/events spawn2 - first create event and set event spawn2\n" +
            "&d/events inventory - first create event and set event inventory\n" +
            "&d/events save - first create event and save settings\n" +
            "&d/events reload - reload this configs and some options\n"
    );

}
