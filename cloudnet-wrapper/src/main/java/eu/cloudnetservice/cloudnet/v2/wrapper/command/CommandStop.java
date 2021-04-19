package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import org.jline.reader.ParsedLine;

public final class CommandStop extends Command {

    public CommandStop() {
        super("stop", "cloudnet.command.stop", "end", "exit");

        description = "Stop this CloudNet-Master application instance";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        CloudNetWrapper.getInstance().getConsoleManager().setRunning(false);
        CloudNetWrapper.getInstance().shutdown();
    }
}
