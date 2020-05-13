package eu.cloudnetservice.v2.master.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import eu.cloudnetservice.v2.master.CloudNet;

import java.io.IOException;

public final class CommandClear extends Command {

    public CommandClear() {
        super("clear", "cloudnet.command.clear");

        description = "Clears the console";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        try {
            CloudNet.getLogger().getReader().clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}