package de.dytanic.cloudnetwrapper.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;

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
