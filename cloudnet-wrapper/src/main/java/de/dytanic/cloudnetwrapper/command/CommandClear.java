/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

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
        }
    }
}
