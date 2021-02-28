package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import org.jline.reader.ParsedLine;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandClearCache extends Command {

    public CommandClearCache() {
        super("clearcache", "cloudnet.command.clearcache", "cc");

        description = "Clears the plugin and template cache of all wrappers";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        CloudNet.getInstance().getWrappers().values().forEach(wrapper -> {
            if (wrapper.getChannel() != null) {
                wrapper.sendCommand("clearcache");
            }
        });
        sender.sendMessage("The caches of all wrappers were cleared");
    }
}
