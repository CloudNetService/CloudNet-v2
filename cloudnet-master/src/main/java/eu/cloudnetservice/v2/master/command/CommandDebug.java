package eu.cloudnetservice.v2.master.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import eu.cloudnetservice.v2.master.CloudNet;

public final class CommandDebug extends Command {

    public CommandDebug() {
        super("debug", "cloudnet.command.debug");

        description = "Toggles the cloudnet debug mode";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNet.getLogger().setDebugging(!CloudNet.getLogger().isDebugging());
        if (CloudNet.getLogger().isDebugging()) {
            sender.sendMessage("debugging was enabled");
        } else {
            sender.sendMessage("debugging was disabled");
        }
    }
}
