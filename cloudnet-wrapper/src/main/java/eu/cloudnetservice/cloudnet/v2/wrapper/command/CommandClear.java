package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

import java.io.IOException;

/**
 * Created by Tareko on 23.09.2017.
 */
public class CommandClear extends Command {

    public CommandClear() {
        super("clear", "cloudnet.command.clear");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNetWrapper.getInstance().getConsoleManager().getLineReader().getTerminal().flush();
    }
}
