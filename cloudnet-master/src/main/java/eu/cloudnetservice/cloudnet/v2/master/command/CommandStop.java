package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

/**
 * Created by Tareko on 30.07.2017.
 */
public final class CommandStop extends Command {

    public CommandStop() {
        super("stop", "cloudnet.command.stop", "end", "exit");

        description = "Stop this CloudNet-Master application instance";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("CloudNetV2 will be stopped...");
        CloudNet.getInstance().shutdown();
    }
}
