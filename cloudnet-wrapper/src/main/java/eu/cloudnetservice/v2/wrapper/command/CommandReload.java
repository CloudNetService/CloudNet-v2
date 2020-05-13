package eu.cloudnetservice.v2.wrapper.command;

import eu.cloudnetservice.v2.examples.command.Command;
import eu.cloudnetservice.v2.examples.command.CommandSender;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.v2.wrapper.network.packet.out.PacketOutUpdateWrapperInfo;

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
