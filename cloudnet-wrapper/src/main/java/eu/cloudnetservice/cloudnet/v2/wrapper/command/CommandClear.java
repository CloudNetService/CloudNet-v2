package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import org.jline.reader.ParsedLine;

public class CommandClear extends Command {

    public CommandClear() {
        super("clear", "cloudnet.command.clear");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine, String[] args) {
        CloudNetWrapper.getInstance().getConsoleManager().getLineReader().getTerminal().flush();
    }
}
