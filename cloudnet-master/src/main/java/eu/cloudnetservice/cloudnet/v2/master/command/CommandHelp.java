package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import org.jline.reader.ParsedLine;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHelp extends Command {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public CommandHelp() {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        List<String> messages = new ArrayList<>(CloudNet.getInstance().getCommandManager().getCommands().size() + 9);

        for (String command : CloudNet.getInstance().getCommandManager().getCommands()) {
            messages.add(command + " | " + CloudNet.getInstance().getCommandManager().getCommand(command).getDescription());
        }

        sender.sendMessage(messages.toArray(EMPTY_STRING_ARRAY));
    }
}
