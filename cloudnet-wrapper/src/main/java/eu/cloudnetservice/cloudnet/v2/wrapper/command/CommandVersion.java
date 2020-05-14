package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;

/**
 * Created by Tareko on 19.01.2018.
 */
public class CommandVersion extends Command {

    public CommandVersion() {
        super("version", "cloudnet.command.version");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("CloudNet " + NetworkUtils.class.getPackage().getSpecificationVersion() + " #" + NetworkUtils.class.getPackage()
                                                                                                                              .getImplementationVersion() + " by Dytanic");
    }
}
