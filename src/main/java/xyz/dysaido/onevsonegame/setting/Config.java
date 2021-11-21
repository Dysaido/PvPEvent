package xyz.dysaido.onevsonegame.setting;

public class Config {

    @Settings.ConfigSetting(path = "message", name = "join")
    public static String JOIN_MESSAGE = "{player} joined the event!";

    @Settings.ConfigSetting(path = "message", name = "leave")
    public static String LEAVE_MESSAGE = "{player} left the event!";

    @Settings.ConfigSetting(path = "message", name = "already_joined")
    public static String ALREADY_JOINED_MESSAGE = "You are already joined!";

    @Settings.ConfigSetting(path = "message", name = "event_not_available")
    public static String EVENT_NOT_AVAILABLE_MESSAGE = "Event not available!";

    @Settings.ConfigSetting(path = "message", name = "nexround")
    public static String NEXT_ROUND = "Round {round}";

    @Settings.ConfigSetting(path = "message", name = "waiting")
    public static String WAITING_MESSAGE = "Join for event {second} sec";

    @Settings.ConfigSetting(path = "message", name = "clickable_broadcast")
    public static String CLICKABLE_MESSAGE = "&b[Click to join]";

    @Settings.ConfigSetting(path = "message", name = "join_finished")
    public static String EVENT_JOIN_FINISHED_MESSAGE = "Event will start";

    @Settings.ConfigSetting(path = "message", name = "starting")
    public static String EVENT_WILL_START_MESSAGE = "Event will be started {second} sec";

    @Settings.ConfigSetting(path = "message", name = "start")
    public static String EVENT_START_MESSAGE = "Event start";

    @Settings.ConfigSetting(path = "message", name = "winner")
    public static String EVENT_WINNER_MESSAGE = "Event's winner {player}";

    @Settings.ConfigSetting(path = "message", name = "ending")
    public static String EVENT_ENDING_MESSAGE = "Event will be ended {second} sec";

    @Settings.ConfigSetting(path = "message", name = "end")
    public static String EVENT_ENDED_MESSAGE = "Event ended!";

    @Settings.ConfigSetting(path = "settings.count", name = "waiting")
    public static int WAITING = 30;

    @Settings.ConfigSetting(path = "settings.count", name = "starting")
    public static int STARTING = 5;

    @Settings.ConfigSetting(path = "settings.count", name = "ending")
    public static int ENDING = 5;

    @Settings.ConfigSetting(path = "settings.bool", name = "freeze")
    public static boolean FREEZE = false;

}
