/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutUpdateWrapperInfo;

public final class CommandReload extends Command {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNetWrapper.getInstance().getWrapperConfig().load();
        CloudNetWrapper.getInstance().setMaxMemory(CloudNetWrapper.getInstance().getWrapperConfig().getMaxMemory());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutUpdateWrapperInfo());
        System.out.println("Reloading was completed successfully");
    }
}
