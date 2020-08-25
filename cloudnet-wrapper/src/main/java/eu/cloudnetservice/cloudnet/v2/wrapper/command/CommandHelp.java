package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

public class CommandHelp extends Command {

    public CommandHelp() {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("You can use the commands:");
        CloudNetWrapper.getInstance().getCommandManager().getCommands().forEach(sender::sendMessage);
    }
}
