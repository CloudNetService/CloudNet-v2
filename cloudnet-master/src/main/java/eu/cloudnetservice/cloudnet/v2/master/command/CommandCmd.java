package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;

public final class CommandCmd extends Command implements TabCompletable {

    public CommandCmd() {
        super("cmd", "cloudnet.command.cmd", "command");

        description = "Executes a command on a game server or proxy server";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            for (short i = 2; i < parsedLine.words().size(); i++) {
                stringBuilder.append(parsedLine.words().get(i)).append(NetworkUtils.SPACE_STRING);
            }
            String command = stringBuilder.substring(0, stringBuilder.length() - 1);
            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                if (wrapper.getName().equalsIgnoreCase(parsedLine.words().get(1))) {
                    wrapper.writeCommand(command);
                    sender.sendMessage("Sending command to " + wrapper.getName() + " with [\"" + command + "\"]");
                    return;
                }

                for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                    if (minecraftServer.getServiceId().getServerId().equalsIgnoreCase(parsedLine.words().get(1))) {
                        minecraftServer.getWrapper().writeServerCommand(command, minecraftServer.getServerInfo());
                        sender.sendMessage("§aSending command to " + minecraftServer.getServiceId()
                                                                                  .getServerId() + " with [\"" + command + "\"]");
                        return;
                    }
                }

                for (ProxyServer minecraftServer : wrapper.getProxies().values()) {
                    if (minecraftServer.getServiceId().getServerId().equalsIgnoreCase(parsedLine.words().get(1))) {
                        minecraftServer.getWrapper().writeProxyCommand(command, minecraftServer.getProxyInfo());
                        sender.sendMessage("§aSending command to " + minecraftServer.getServiceId()
                                                                                  .getServerId() + " with [\"" + command + "\"]");
                        return;
                    }
                }
            }
        } else {
            sender.sendMessage("cmd <name> <command> | Executes a command, either from a wrapper, proxy or game server");
        }
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers().values()) {
            candidates.add(new Candidate(minecraftServer.getName(), minecraftServer.getName(), minecraftServer.getGroup().getName(), "A minecraft server", null ,null, true));
        }
        for (ProxyServer proxyServer : CloudNet.getInstance().getProxys().values()) {
            candidates.add(new Candidate(proxyServer.getName(), proxyServer.getName(), proxyServer.getProcessMeta().getProxyGroupName(), "A proxy", null ,null, true));
        }
        return candidates;
    }
}
