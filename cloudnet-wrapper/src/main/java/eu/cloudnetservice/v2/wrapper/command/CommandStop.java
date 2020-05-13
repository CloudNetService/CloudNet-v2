package eu.cloudnetservice.v2.wrapper.command;

import eu.cloudnetservice.v2.examples.command.Command;
import eu.cloudnetservice.v2.examples.command.CommandSender;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

public class CommandStop extends Command {

    public CommandStop() {
        super("stop", "cloudnet.command.stop", "end", "exit");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNetWrapper.getInstance().shutdown();
    }
}
