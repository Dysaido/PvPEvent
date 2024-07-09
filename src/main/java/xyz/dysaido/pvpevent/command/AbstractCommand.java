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

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.util.BukkitHelper;
import xyz.dysaido.pvpevent.util.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCommand extends Command {

    private static final String TAG = "CommandManager";
    protected final PvPEventPlugin pvpEvent;
    protected final Map<String, SubCommand<CommandSender>> subcommands = new HashMap<>();

    public AbstractCommand(PvPEventPlugin pvpEvent, String name) {
        super(name);
        this.pvpEvent = pvpEvent;
    }

    public SubCommand<CommandSender> register(CommandInfo info, Function<CommandInfo, SubCommand<CommandSender>> event) {
        String name = info.getName();
        if (this.subcommands.containsKey(name)) {
            Logger.error(TAG, String.format("RegisterError - MainCommand: %s, subcommand: %s", getName(), name));
            throw new RuntimeException("You cannot register same command that registered");
        } else {
            Logger.debug(TAG, String.format("Register - MainCommand: %s, subcommand: %s", getName(), name));
            SubCommand<CommandSender> subcommand = event.apply(info);
            this.subcommands.put(name.toLowerCase(), subcommand);
            return subcommand;
        }
    }

    public void unregister(String name) {
        Logger.debug(TAG, String.format("Unregister - MainCommand: %s, subcommand: %s", getName(), name));
        this.subcommands.remove(name);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0 && subcommands.containsKey("")) {
            subcommands.get("").execute(sender, args);
            return true;
        }

        String arrToStr = String.join(" ", args).toLowerCase();
        Optional<Map.Entry<String, SubCommand<CommandSender>>> matchedCommand =
                subcommands.entrySet().stream()
                        .filter(entry -> !entry.getKey().isEmpty())
                        .filter(entry -> {
                            String alias = entry.getValue().getAlias();
                            boolean aliasCheck = !alias.isEmpty() && arrToStr.startsWith(entry.getValue().getAlias());
                            return arrToStr.startsWith(entry.getKey()) || aliasCheck;
                        })
                        .findAny();
        if (matchedCommand.isPresent()) {
            SubCommand<CommandSender> subcommand = matchedCommand.get().getValue();
            if (isAuthorized(sender, subcommand)) {
                String[] param = args.length == 0 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
                subcommand.execute(sender, param);
            } else {
                sender.sendMessage(MESSAGE.NO_PERMISSION.format());
            }
        } else
            sender.sendMessage(ChatColor.RED + "Unknown command. Please try /event or /event help.");
        return true;
    }

    protected boolean isAuthorized(CommandSender sender, SubCommand<CommandSender> subcommand) {
        return sender.hasPermission(subcommand.getPerm()) || sender.isOp() || sender.hasPermission(Settings.IMP.PERMISSION.COMMAND_ADMIN);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String subcommand = args[0].toLowerCase(Locale.ROOT);
            return Stream.concat(
                            Stream.of(CommandInfo.getDefaults()),
                            sender.hasPermission(Settings.IMP.PERMISSION.COMMAND_ADMIN)
                                    ? Stream.of(CommandInfo.getStaffs()) : Stream.empty()
                    )
                    .map(CommandInfo::getName)
                    .filter(s -> s.startsWith(subcommand))
                    .collect(Collectors.toList());
        } else {
            if (args.length >= 2) {
                String subcommand = args[0].toLowerCase(Locale.ROOT);
                String option1 = args[1].toLowerCase(Locale.ROOT);
                if (args.length == 3) {
                    String option2 = args[2].toLowerCase(Locale.ROOT);
                    if (subcommand.equals("editarena")) {
                        return Stream.of(CommandInfo.EDITARENA.getArguments())
                                .filter(s -> s.startsWith(option2))
                                .collect(Collectors.toList());
                    } else if (subcommand.equals("editkit")) {
                        return Stream.of(CommandInfo.EDITKIT.getArguments())
                                .filter(s -> s.startsWith(option2))
                                .collect(Collectors.toList());
                    }
                } else if (subcommand.equals("host")
                        || subcommand.equals("editarena")
                        || subcommand.equals("delarena")
                        || subcommand.equals("autoset")) {
                    List<String> arenas = new ArrayList<>(pvpEvent.getArenaManager().getAll().keySet());
                    return StringUtil.copyPartialMatches(option1, arenas, new ArrayList<>(arenas.size()));
                } else if (subcommand.equals("editkit")
                        || subcommand.equals("delkit")) {
                    List<String> kits = new ArrayList<>(pvpEvent.getKitManager().getAll().keySet());
                    return StringUtil.copyPartialMatches(option1, kits, new ArrayList<>(kits.size()));
                } else if (subcommand.equals("reload")) {
                        return Stream.of(CommandInfo.RELOAD.getArguments())
                                .filter(s -> s.startsWith(option1))
                                .collect(Collectors.toList());
                }
                return super.tabComplete(sender, alias, args);
            }
            return ImmutableList.of();
        }
    }

    public enum MESSAGE {
        NO_PERMISSION("&cYou don't have permission to perform this command!"),
        ONLY_CONSOLE("&cYou don't have permission to perform this command!"),
        ONLY_PLAYER("&cConsole don't authorized to perform this command, because you are not an entity!");

        private final String msg;

        MESSAGE(String msg) {
            this.msg = msg;
        }

        public String format() {
            return BukkitHelper.colorize(msg);
        }

    }
}