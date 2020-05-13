package eu.cloudnetservice.v2.master.command;

import eu.cloudnetservice.v2.command.Command;
import eu.cloudnetservice.v2.command.CommandSender;
import eu.cloudnetservice.v2.lib.NetworkUtils;

/**
 * Created by Tareko on 19.01.2018.
 */
public final class CommandVersion extends Command {

    public CommandVersion() {
        super("version", "cloudnet.command.version");

        description = "Shows the version of this instance";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("CloudNet " + NetworkUtils.class.getPackage().getSpecificationVersion() + " #" + NetworkUtils.class.getPackage()
                                                                                                                              .getImplementationVersion() + " by Dytanic");
    }
}
