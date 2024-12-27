/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.dysaido.pvpevent.command;

import java.util.Arrays;

public enum CommandInfo {
    NONE(false, "", "/event", "Base event command"),
    HELP(false, "help", "/event help", "Assistance for staff members"),
    JOIN(false, "join", "/event join", "Participate in the ongoing event"),
    LEAVE(false, "leave", "/event leave", "Withdraw from the event"),
    SPECTATE(false, "spectate", "/event spectate", "Observe the current event as a spectator"),
    TOPLIST(false, "toplist", "/event toplist", "See top wins"),

    VIEW(true, "view", "/event view [gui(alpha)]", "Access a graphical user interface for editing"),
    HOST(true, "host", "/event host [arena] [broadcast]", "Initiate a saved event"),
    STOP(true, "stop", "/event stop", "Terminate the ongoing event"),
    RELOAD(true, "reload", "/event reload [config|full]", "Reload the entire plugin, including files",
            "config", "full"),

    KICK(true, "kick", "/event kick [user]", "Remove a user from the event"),
    BAN(true, "ban", "/event ban [user]", "Exclude a user from the event"),
    UNBAN(true, "unban", "/event unban [user]", "Revoke the ban for a user from the event"),

    CREATEARENA(true, "createarena", "/event createarena [name]", "Create a new empty arena"),
    EDITARENA(true, "editarena", "/event editarena [name] [argument]",
            "Modify the parameters of an existing arena",
            "setlobby", "setpos1", "setpos2",
            "setkit", "mincapacity","capacity", "queuetimer", "fighttimer", "invtoggle", "combomode"),
    DELARENA(true, "delarena", "/event delarena [name]", "Delete an arena from the database"),

    CREATEKIT(true, "createkit", "/event createkit [name]", "Create a new empty kit"),
    EDITKIT(true, "editkit", "/event editkit [name]", "Edit the contents of an existing kit",
            "setinventory"),
    DELKIT(true, "delkit", "/event delkit [name]", "Delete a kit from the database");

    private static final CommandInfo[] defaults = Arrays.stream(values()).filter(commandInfo -> !commandInfo.isStaff()).toArray(CommandInfo[]::new);
    private static final CommandInfo[] staffs = Arrays.stream(values()).filter(CommandInfo::isStaff).toArray(CommandInfo[]::new);
    private final boolean staff;
    private final String name;
    private final String usage;
    private final String description;
    private final String[] arguments;

    CommandInfo(boolean staff, String name, String usage, String description, String... args) {
        this.staff = staff;
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.arguments = args;
    }

    public static CommandInfo[] getStaffs() {
        return staffs;
    }

    public static CommandInfo[] getDefaults() {
        return defaults;
    }

    public boolean isStaff() {
        return staff;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String[] getArguments() {
        return arguments;
    }
}
