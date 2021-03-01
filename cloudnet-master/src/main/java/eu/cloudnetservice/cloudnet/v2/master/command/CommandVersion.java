package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import org.jline.reader.ParsedLine;

public final class CommandVersion extends Command {

    public CommandVersion() {
        super("version", "cloudnet.command.version");

        description = "Shows the version of this instance";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        sender.sendMessage("CloudNet " + NetworkUtils.class.getPackage().getSpecificationVersion() + " #" + NetworkUtils.class.getPackage()
                                                                                                                              .getImplementationVersion() + " by Cloud§9Net §rCommunity");
    }
}
