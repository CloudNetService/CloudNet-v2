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

/**
 * Created by Tareko on 26.07.2017.
 */
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

        messages.add(NetworkUtils.SPACE_STRING);
        messages.add("Server groups:");
        messages.add(Arrays.toString(CloudNet.getInstance().getServerGroups().keySet().toArray(EMPTY_STRING_ARRAY)));
        messages.add("Proxy groups:");
        messages.add(Arrays.toString(CloudNet.getInstance().getProxyGroups().keySet().toArray(EMPTY_STRING_ARRAY)));
        messages.add(NetworkUtils.SPACE_STRING);
        messages.add(String.format("The Cloud uses %d/%dMB",
                                   ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L,
                                   ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L));
        messages.add("CPU on this instance " + new DecimalFormat("##.##").format(NetworkUtils.internalCpuUsage()) + "/100 %");
        messages.add(NetworkUtils.SPACE_STRING);

        sender.sendMessage(messages.toArray(EMPTY_STRING_ARRAY));
    }
}
