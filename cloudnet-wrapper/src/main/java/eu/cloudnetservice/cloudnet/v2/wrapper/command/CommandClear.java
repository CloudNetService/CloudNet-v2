package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import org.jline.reader.ParsedLine;

public final class CommandClear extends Command {

    public CommandClear() {
        super("clear", "cloudnet.command.clear");
        description = "Clears the console";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        CloudNetWrapper.getInstance().getConsoleManager().getLineReader().getTerminal().flush();
    }
}
