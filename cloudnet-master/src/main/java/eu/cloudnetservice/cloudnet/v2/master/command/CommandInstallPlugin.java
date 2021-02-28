package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import org.jline.reader.ParsedLine;

/**
 * Created by Tareko on 27.08.2017.
 */
public final class CommandInstallPlugin extends Command {

    public CommandInstallPlugin() {
        super("installplugin", "cloudnet.command.installplugin");

        description = "Installs plugin onto a server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        switch (parsedLine.wordIndex()) {
            case 3:
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(parsedLine.words().get(0));
                if (minecraftServer != null && minecraftServer.getChannel() != null) {
                    minecraftServer.sendCustomMessage("cloudnet_internal",
                                                      "install_plugin",
                                                      new Document("name", parsedLine.words().get(1)).append("url", parsedLine.words().get(2)));
                    sender.sendMessage("Plugin will install on " + parsedLine.words().get(0) + "...");
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
