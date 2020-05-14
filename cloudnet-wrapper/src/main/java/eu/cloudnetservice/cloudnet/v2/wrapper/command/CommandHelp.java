package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;

public class CommandHelp extends Command {

    public CommandHelp() {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("You can use the commands \"reload\", \"clear\", \"stop\", \"version\" and \"clearcache\"");
    }
}
