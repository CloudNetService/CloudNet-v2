package eu.cloudnetservice.v2.wrapper.command;

import eu.cloudnetservice.v2.command.Command;
import eu.cloudnetservice.v2.command.CommandSender;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

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
        try {
            CloudNetWrapper.getInstance().getCloudNetLogging().getReader().clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
