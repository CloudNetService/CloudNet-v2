/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

public final class CommandDebug extends Command {

    public CommandDebug() {
        super("debug", "cloudnet.command.debug");

        description = "Toggles the cloudnet debug mode";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNet.getLogger().setDebugging(!CloudNet.getLogger().isDebugging());
        if (CloudNet.getLogger().isDebugging()) {
            sender.sendMessage("debugging was enabled");
        } else {
            sender.sendMessage("debugging was disabled");
        }
    }
}
