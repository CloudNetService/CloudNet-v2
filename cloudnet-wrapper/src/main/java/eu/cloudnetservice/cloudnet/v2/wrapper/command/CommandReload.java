package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutUpdateWrapperInfo;
import org.jline.reader.ParsedLine;

public final class CommandReload extends Command {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");
        description = "Reloads the config";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        CloudNetWrapper.getInstance().getWrapperConfig().load();
        CloudNetWrapper.getInstance().setMaxMemory(CloudNetWrapper.getInstance().getWrapperConfig().getMaxMemory());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutUpdateWrapperInfo());
        System.out.println("Reloading was completed successfully");
    }
}
