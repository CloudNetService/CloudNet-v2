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

public final class CommandShutdown extends Command implements TabCompletable {

    public CommandShutdown() {
        super("shutdown", "cloudnet.command.shutdown");

        description = "Stops all wrappers, proxies, servers or proxy/server groups";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() == 3) {
            if (parsedLine.words().get(1).equalsIgnoreCase("wrapper")) {
                if (CloudNet.getInstance().getWrappers().containsKey(parsedLine.words().get(2))) {
                    Wrapper wrapper = CloudNet.getInstance().getWrappers().get(parsedLine.words().get(2));
                    if (wrapper.getChannel() != null) {
                        wrapper.writeCommand("stop");
                    }
                    sender.sendMessage("Wrapper " + parsedLine.words().get(2) + " was stopped");
                } else {
                    sender.sendMessage("Wrapper doesn't exist");
                }
                return;
            }
            if (parsedLine.words().get(1).equalsIgnoreCase("group")) {
                if (CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(2))) {
                    System.out.println("All servers of the server group " + parsedLine.words().get(2) + " will be stopped...");
                    CloudNet.getInstance().getServers(parsedLine.words().get(2)).forEach(server -> {
                        server.getWrapper().stopServer(server);
                        NetworkUtils.sleepUninterruptedly(1000);
                    });
                    return;
                }
                if (CloudNet.getInstance().getProxyGroups().containsKey(parsedLine.words().get(2))) {
                    System.out.println("All proxies of the proxy group " + parsedLine.words().get(2) + " will be stopped");
                    CloudNet.getInstance().getProxys(parsedLine.words().get(2)).forEach(proxy -> {
                        proxy.getWrapper().stopProxy(proxy);
                        NetworkUtils.sleepUninterruptedly(1000);
                    });
                    return;
                }

                sender.sendMessage("Group doesn't exist");
                return;
            }
            if (parsedLine.words().get(1).equalsIgnoreCase("server") || parsedLine.words().get(1).equalsIgnoreCase("-s")) {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(parsedLine.words().get(2));
                if (minecraftServer != null) {
                    minecraftServer.getWrapper().stopServer(minecraftServer);
                    sender.sendMessage("Server " + parsedLine.words().get(2) + " was stopped!");
                } else {
                    sender.sendMessage("The server doesn't exist");
                }
                return;
            }
            if (parsedLine.words().get(1).equalsIgnoreCase("proxy") || parsedLine.words().get(1).equalsIgnoreCase("-p")) {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(parsedLine.words().get(2));
                if (proxyServer != null) {
                    proxyServer.getWrapper().stopProxy(proxyServer);
                    sender.sendMessage("Proxy server " + parsedLine.words().get(2) + " was stopped!");
                } else {
                    sender.sendMessage("The proxy doesn't exist");
                }
            }
        } else {
            sender.sendMessage(NetworkUtils.SPACE_STRING,
                               "shutdown WRAPPER <wrapper-id> | Stops a wrapper service with the respective \"wrapper ID\"",
                               "shutdown GROUP <group-id> | Stops a group of either proxy or servergroup, and restarts it by default",
                               "shutdown PROXY <proxy-id> | Stops a BungeeCord service and after preconfiguring the group, a new one is started",
                               "shutdown SERVER <server-id> | Stops a Minecraft server service and after preconfiguring the group, a new one is started",
                               NetworkUtils.SPACE_STRING);
        }
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> strings = new ArrayList<>();
        if (parsedLine.words().size() == 1) {
            if (parsedLine.words().get(0).equalsIgnoreCase("shutdown")) {
                strings.add(new Candidate("WRAPPER", "WRAPPER", null, "Shutdown a wrapper", null, null, true));
                strings.add(new Candidate("GROUP", "GROUP", null, "Shutdown a group", null, null, true));
                strings.add(new Candidate("PROXY", "PROXY", null, "Shutdown a proxy", null, null, true));
                strings.add(new Candidate("SERVER", "SERVER", null, "Shutdown a SERVER", null, null, true));
            }
        }
        if (parsedLine.words().size() == 2) {
            if (parsedLine.words().get(0).equalsIgnoreCase("shutdown")) {
                if (parsedLine.words().get(1).equalsIgnoreCase("wrapper")) {
                    for (String wrapperId : CloudNet.getInstance().getWrappers().keySet()) {
                        strings.add(new Candidate(wrapperId, wrapperId, null, "Shutdown a wrapper", null, null, true));
                    }
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("GROUP")) {
                    for (String serverGroup : CloudNet.getInstance().getServerGroups().keySet()) {
                        strings.add(new Candidate(serverGroup, serverGroup, "ServerGroups", "Shutdown a group", null, null, true));
                    }
                    for (String proxyGroup : CloudNet.getInstance().getProxyGroups().keySet()) {
                        strings.add(new Candidate(proxyGroup, proxyGroup, "ProxyGroups", "Shutdown a group", null, null, true));
                    }
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("PROXY")) {
                    for (String proxyId : CloudNet.getInstance().getProxys().keySet()) {
                        strings.add(new Candidate(proxyId, proxyId, null, "Shutdown a proxy", null, null, true));
                    }
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("SERVER")) {
                    for (String serverId : CloudNet.getInstance().getServers().keySet()) {
                        strings.add(new Candidate(serverId, serverId, null, "Shutdown a server", null, null, true));
                    }
                }
            }
        }
        return strings;
    }
}
