package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot.SetupSpigotVersion;
import org.jline.reader.ParsedLine;

public final class CommandSetup extends Command {

    private final CloudNetWrapper cloudNetWrapper;

    public CommandSetup(CloudNetWrapper cloudNetWrapper) {
        super("setup", "cloudnet.wrapper.command.setup");
        this.cloudNetWrapper = cloudNetWrapper;
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, ParsedLine parsedLine) {
        cloudNetWrapper.getConsoleManager().getConsoleRegistry().registerInput(new SetupSpigotVersion(cloudNetWrapper.getConsoleManager()));
        cloudNetWrapper.getConsoleManager().changeConsoleInput(SetupSpigotVersion.class);
    }
}
