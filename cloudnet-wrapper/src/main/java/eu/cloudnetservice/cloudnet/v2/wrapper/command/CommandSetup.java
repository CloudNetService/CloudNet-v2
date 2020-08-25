package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot.SetupSpigotVersion;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

public class CommandSetup extends Command {

    private final CloudNetWrapper cloudNetWrapper;

    public CommandSetup(CloudNetWrapper cloudNetWrapper) {
        super("setup", "cloudnet.wrapper.command.setup", "");
        this.cloudNetWrapper = cloudNetWrapper;
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        cloudNetWrapper.getConsoleManager().getConsoleRegistry().registerInput(new SetupSpigotVersion(cloudNetWrapper.getConsoleManager()));
        cloudNetWrapper.getConsoleManager().changeConsoleInput(SetupSpigotVersion.class);
    }
}
