/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 30.07.2017.
 */
public final class CommandStop extends Command {

    public CommandStop() {
        super("stop", "cloudnet.command.stop", "end", "exit");

        description = "Stop this CloudNet-Master application instance";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("CloudNetV2 will be stopped...");
        CloudNet.getInstance().shutdown();
    }
}
