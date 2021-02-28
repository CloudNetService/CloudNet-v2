package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import org.jline.reader.ParsedLine;

public final class CommandClear extends Command {

    public CommandClear() {
        super("clear", "cloudnet.command.clear");

        description = "Clears the console";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        CloudNet.getInstance().getConsoleManager().getLineReader().getTerminal().flush();
    }
}
