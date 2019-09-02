/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class CommandStop extends Command {

    public CommandStop() {
        super("stop", "cloudnet.command.stop", "exit");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNetWrapper.getInstance().shutdown();
    }
}
