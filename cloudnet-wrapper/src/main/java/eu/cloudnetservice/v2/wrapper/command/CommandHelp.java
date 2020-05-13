package eu.cloudnetservice.v2.wrapper.command;

import eu.cloudnetservice.v2.examples.command.Command;
import eu.cloudnetservice.v2.examples.command.CommandSender;

public class CommandHelp extends Command {

    public CommandHelp() {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("You can use the commands \"reload\", \"clear\", \"stop\", \"version\" and \"clearcache\"");
    }
}
