package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import org.jline.reader.ParsedLine;

public final class CommandDebug extends Command {

    public CommandDebug() {
        super("debug", "cloudnet.command.debug");

        description = "Toggles the cloudnet debug mode";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        CloudNet.getLogger().setDebugging(!CloudNet.getLogger().isDebugging());
        if (CloudNet.getLogger().isDebugging()) {
            sender.sendMessage("debugging was enabled");
        } else {
            sender.sendMessage("debugging was disabled");
        }
    }
}
