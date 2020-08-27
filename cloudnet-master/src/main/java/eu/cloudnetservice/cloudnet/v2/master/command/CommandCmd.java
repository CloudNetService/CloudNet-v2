package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.ParsedLine;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class CommandCmd extends Command {

    public CommandCmd() {
        super("cmd", "cloudnet.command.cmd", "command");

        description = "Executes a command on a game server or proxy server";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine, String[] args) {
        if (args.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            for (short i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);
            }
            String command = stringBuilder.substring(0, stringBuilder.length() - 1);
            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                if (wrapper.getName().equalsIgnoreCase(args[0])) {
                    wrapper.writeCommand(command);
                    sender.sendMessage("Sending command to " + wrapper.getName() + " with [\"" + command + "\"]");
                    return;
                }

                for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                    if (minecraftServer.getServiceId().getServerId().equalsIgnoreCase(args[0])) {
                        minecraftServer.getWrapper().writeServerCommand(command, minecraftServer.getServerInfo());
                        sender.sendMessage("Sending command to " + minecraftServer.getServiceId()
                                                                                  .getServerId() + " with [\"" + command + "\"]");
                        return;
                    }
                }

                for (ProxyServer minecraftServer : wrapper.getProxies().values()) {
                    if (minecraftServer.getServiceId().getServerId().equalsIgnoreCase(args[0])) {
                        minecraftServer.getWrapper().writeProxyCommand(command, minecraftServer.getProxyInfo());
                        sender.sendMessage("Sending command to " + minecraftServer.getServiceId()
                                                                                  .getServerId() + " with [\"" + command + "\"]");
                        return;
                    }
                }
            }
        } else {
            sender.sendMessage("cmd <name> <command> | Executes a command, either from a wrapper, proxy or game server");
        }
    }
}
