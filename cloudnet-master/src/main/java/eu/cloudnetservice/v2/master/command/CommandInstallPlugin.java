package eu.cloudnetservice.v2.master.command;

import eu.cloudnetservice.v2.command.Command;
import eu.cloudnetservice.v2.command.CommandSender;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;

/**
 * Created by Tareko on 27.08.2017.
 */
public final class CommandInstallPlugin extends Command {

    public CommandInstallPlugin() {
        super("installplugin", "cloudnet.command.installplugin");

        description = "Installs plugin onto a server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        switch (args.length) {
            case 3:
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if (minecraftServer != null && minecraftServer.getChannel() != null) {
                    minecraftServer.sendCustomMessage("cloudnet_internal",
                                                      "install_plugin",
                                                      new Document("name", args[1]).append("url", args[2]));
                    sender.sendMessage("Plugin will install on " + args[0] + "...");
                } else {
                    sender.sendMessage("Server doesn't exist");
                }
                break;
            default:
                sender.sendMessage("installplugin <server> <name> <url>");
                break;
        }
    }
}
