package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.console.completer.CloudNetCompleter;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.logging.color.ChatColor;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutConsoleSettings;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.ArgumentCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandConsole extends Command implements TabCompletable {

    public CommandConsole() {
        super("console", "cloudnet.command.master.console");
        description = "Allows to customize some console options";
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final ParsedLine parsedLine) {
        if (parsedLine.words().size() <= 2) {
            sender.sendMessage("§8console §a<option> §9<value> §8| Manipulate a option for the console");
            sender.sendMessage("Options: ");
            sender.sendMessage("- showmenu | Allows to disable or enable a selection menu for tab completion");
            sender.sendMessage("- showdescription | Allows to disable or enable description for tab completion");
            sender.sendMessage("- showgroup | Allows to disable or enable grouping for tab completion");
            sender.sendMessage("- autolist | Allows to disable or enable auto list for tab completion");
            sender.sendMessage("- aliases | Allows to disable or enable aliases tab completion");
            sender.sendMessage("- elof | Allows to disable or enable erasing line on finish");
            sender.sendMessage("- color | Allows to change the color of the default tab completion");
            sender.sendMessage("- groupcolor | Allows to change the grouping color of the default tab completion");
        } else if (parsedLine.words().size() == 3) {
            switch (parsedLine.words().get(1).toLowerCase()) {
                case "showdescription":
                    boolean parseBoolean = Boolean.parseBoolean(parsedLine.words().get(2));
                    CloudNet.getInstance().getConfig().setShowDescription(parseBoolean);
                    LineReader lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    if (lineReader instanceof LineReaderImpl) {
                        Completer completer = ((LineReaderImpl) lineReader).getCompleter();
                        if (completer instanceof CloudNetCompleter) {
                            ((CloudNetCompleter) completer).setShowDescription(parseBoolean);
                        }
                    }
                    CloudNet.getInstance().getCommandManager().setShowDescription(parseBoolean);
                    sender.sendMessage("§aUpdate visibility of description on tab completion to: " + parseBoolean);
                    updateWrappers();
                    break;
                case "showmenu":
                    parseBoolean = Boolean.parseBoolean(parsedLine.words().get(2));
                    lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    lineReader.option(LineReader.Option.MENU_COMPLETE, parseBoolean);
                    lineReader.option(LineReader.Option.AUTO_MENU, parseBoolean);
                    CloudNet.getInstance().getConfig().setShowMenu(parseBoolean);
                    sender.sendMessage("§aUpdate visibility of menu on tab completion to: " + parseBoolean);
                    updateWrappers();
                    break;
                case "showgroup":
                    parseBoolean = Boolean.parseBoolean(parsedLine.words().get(2));
                    lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    lineReader.option(LineReader.Option.GROUP, parseBoolean);
                    lineReader.option(LineReader.Option.AUTO_GROUP, parseBoolean);
                    CloudNet.getInstance().getConfig().setShowGroup(parseBoolean);
                    sender.sendMessage("§aUpdate visibility of grouping on tab completion to: " + parseBoolean);
                    updateWrappers();
                    break;
                case "autolist":
                    parseBoolean = Boolean.parseBoolean(parsedLine.words().get(2));
                    lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    lineReader.option(LineReader.Option.AUTO_LIST, parseBoolean);
                    CloudNet.getInstance().getConfig().setAutoList(parseBoolean);
                    sender.sendMessage("§aUpdate visibility of auto listining on tab completion to: " + parseBoolean);
                    updateWrappers();
                    break;
                case "elof":
                    parseBoolean = Boolean.parseBoolean(parsedLine.words().get(2));
                    lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    lineReader.option(LineReader.Option.ERASE_LINE_ON_FINISH, parseBoolean);
                    CloudNet.getInstance().getConfig().setElof(parseBoolean);
                    sender.sendMessage("§aUpdate option of erase on line finish to: " + parseBoolean);
                    updateWrappers();
                    break;
                case "aliases":
                    parseBoolean = Boolean.parseBoolean(parsedLine.words().get(2));
                    CloudNet.getInstance().getConfig().setAliases(parseBoolean);
                    CloudNet.getInstance().getCommandManager().setAliases(parseBoolean);
                    sender.sendMessage("§aUpdate option of aliases to: " + parseBoolean);
                    updateWrappers();
                    break;
                case "color":
                    ChatColor color = ChatColor.getByChar(parsedLine.words().get(2).charAt(1));
                    lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    if (lineReader instanceof LineReaderImpl) {
                        Completer completer = ((LineReaderImpl) lineReader).getCompleter();
                        if (completer instanceof ArgumentCompleter) {
                            ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setColor(color.toString());
                        }
                    }
                    CloudNet.getInstance().getConfig().setColor(color.toString());
                    sender.sendMessage("§aUpdate color to: " + color.toString() + color.getName());
                    updateWrappers();
                    break;
                case "groupcolor":
                    color = ChatColor.getByChar(parsedLine.words().get(2).charAt(1));
                    lineReader = CloudNet.getInstance().getConsoleManager().getLineReader();
                    if (lineReader instanceof LineReaderImpl) {
                        Completer completer = ((LineReaderImpl) lineReader).getCompleter();
                        if (completer instanceof ArgumentCompleter) {
                            ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setGroupColor(color.toString());
                        }
                    }
                    CloudNet.getInstance().getConfig().setGroupColor(color.toString());
                    sender.sendMessage("§aUpdate group color to: " + color.toString() + color.getName());
                    updateWrappers();
                    break;
                default:
                    sender.sendMessage("§cThis option is not available!");
                    break;
            }
        }

    }

    @Override
    public List<Candidate> onTab(final ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();

        if (parsedLine.words().size() == 1 && containsAlias(parsedLine.words().get(0)) || name.equalsIgnoreCase(parsedLine.words().get(0))) {
            if (parsedLine.words().size() >= 2) {
                switch (parsedLine.words().get(1).toLowerCase()) {
                    case"showdescription":
                    case"showmenu":
                    case"showgroup":
                    case"autolist":
                    case"elof":
                        candidates.add(new Candidate("true", "§9True", "Value", "Enable option", null, null, true));
                        candidates.add(new Candidate("false", "§9False", "Value", "Disable option", null, null, true));
                    break;
                    case "groupcolor":
                    case "color":
                        candidates.addAll(Arrays.stream(ChatColor.values())
                                                .map(chatColor -> new Candidate(chatColor.toString(),
                                                                                chatColor.toString() + chatColor.getName(),
                                                                                "Color",
                                                                                "Color to choose",
                                                                                null,
                                                                                null,
                                                                                true))
                                                .collect(
                                                    Collectors.toList()));
                        break;
                }
            } else {
                candidates.add(new Candidate("showdescription",
                                             "§aShow description",
                                             "Option",
                                             "Allows to disable or enable description for tab completion",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("showmenu",
                                             "§aShow menu",
                                             "Option",
                                             "Allows to disable or enable a selection menu for tab completion",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("showgroup",
                                             "§aShow group",
                                             "Option",
                                             "Allows to disable or enable grouping for tab completion",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("autolist",
                                             "§aAutolist",
                                             "Option",
                                             "Allows to disable or enable auto list for tab completion",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("elof",
                                             "§aErasing line on finish",
                                             "Option",
                                             "Allows to disable or enable erasing line on finish",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("color",
                                             "§aColor",
                                             "Option",
                                             "Allows to change the color of the default tab completion",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("groupcolor",
                                             "§aGroup color",
                                             "Option",
                                             "Allows to change the grouping color of the default tab completion",
                                             null,
                                             null,
                                             true));
                candidates.add(new Candidate("aliases",
                                             "§aAliases",
                                             "Option",
                                             "Allows to disable or enable aliases tab completion",
                                             null,
                                             null,
                                             true));
            }

            return candidates;
        }

        return candidates;
    }

    private void updateWrappers() {
        CloudNet.getInstance().getWrappers().values().forEach(
            wrapper -> wrapper.sendPacket(new PacketOutConsoleSettings(new Document()
                                                                           .append("console",
                                                                                   new Document()
                                                                                       .append("aliases", CloudNet.getInstance().getConfig().isAliases())
                                                                                       .append("showdescription", CloudNet.getInstance().getConfig().isShowDescription())
                                                                                       .append("showgroup", CloudNet.getInstance().getConfig().isShowGroup())
                                                                                       .append("elof", CloudNet.getInstance().getConfig().isElof())
                                                                                       .append("showmenu", CloudNet.getInstance().getConfig().isShowMenu())
                                                                                       .append("autolist", CloudNet.getInstance().getConfig().isAutoList())
                                                                                       .append("groupcolor", CloudNet.getInstance().getConfig().getGroupColor())
                                                                                       .append("color", CloudNet.getInstance().getConfig().getColor())
                                                                           )))
        );
    }
}
