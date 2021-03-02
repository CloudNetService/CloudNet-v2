package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandInfo extends Command implements TabCompletable {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public CommandInfo() {
        super("info", "cloudnet.command.info", "i");

        description = "Shows information about a server, proxy, wrapper or server group";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() == 3) {
            switch (parsedLine.words().get(1).toLowerCase()) {
                case "server": {
                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(parsedLine.words().get(2));
                    if (minecraftServer == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "Server: " + minecraftServer.getServiceId().getServerId(),
                                       "UUID: " + minecraftServer.getServiceId().getUniqueId(),
                                       "Group: " + minecraftServer.getServiceId().getGroup(),
                                       NetworkUtils.SPACE_STRING,
                                       "Connection: " + (minecraftServer.getChannel() != null ? "connected" : "not connected"),
                                       "State: " + (minecraftServer.getServerInfo()
                                                                   .isOnline() ? "Online" : "Offline") + " | " + minecraftServer.getServerInfo()
                                                                                                                                .getServerState(),
                                       "Online: " + minecraftServer.getServerInfo()
                                                                   .getOnlineCount() + NetworkUtils.SLASH_STRING + minecraftServer.getServerInfo()
                                                                                                                                  .getMaxPlayers(),
                                       "Motd: " + minecraftServer.getServerInfo().getMotd(),
                                       NetworkUtils.SPACE_STRING,
                                       "Memory: " + minecraftServer.getServerInfo().getMemory(),
                                       "Address: " + minecraftServer.getServerInfo().getHost(),
                                       "Port: " + minecraftServer.getServerInfo().getPort(),
                                       NetworkUtils.SPACE_STRING);
                }
                break;
                case "proxy": {
                    ProxyServer proxyServer = CloudNet.getInstance().getProxy(parsedLine.words().get(2));
                    if (proxyServer == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "Proxy: " + proxyServer.getServiceId().getServerId(),
                                       "UUID: " + proxyServer.getServiceId().getUniqueId(),
                                       "Group: " + proxyServer.getServiceId().getGroup(),
                                       NetworkUtils.SPACE_STRING,
                                       "Connection: " + (proxyServer.getChannel() != null ? "connected" : "not connected"),
                                       "State: " + (proxyServer.getProxyInfo().isOnline() ? "Online" : "Offline"),
                                       "Online: " + proxyServer.getProxyInfo().getOnlineCount(),
                                       NetworkUtils.SPACE_STRING,
                                       "Memory: " + proxyServer.getProxyInfo().getMemory(),
                                       "Address: " + proxyServer.getProxyInfo().getHost(),
                                       "Port: " + proxyServer.getProxyInfo().getPort(),
                                       NetworkUtils.SPACE_STRING);
                }
                break;
                case "wrapper": {
                    Wrapper wrapper = CloudNet.getInstance().getWrappers().get(parsedLine.words().get(2));
                    if (wrapper == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "WrapperId: " + wrapper.getServerId(),
                                       NetworkUtils.SPACE_STRING,
                                       "Connection: " + (wrapper.getChannel() != null ? "connected" : "not connected"),
                                       "Servers started: " + wrapper.getServers().size(),
                                       "Proxys started: " + wrapper.getProxies().size(),
                                       NetworkUtils.SPACE_STRING,
                                       "Address: " + wrapper.getNetworkInfo().getHostName(),
                                       "Memory: " + wrapper.getUsedMemoryAndWaitings() + NetworkUtils.SLASH_STRING + (wrapper.getWrapperInfo() != null ? wrapper
                                           .getWrapperInfo()
                                           .getMemory() + NetworkUtils.EMPTY_STRING : "0") + "MB",
                                       "CPU Cores: " + (wrapper.getWrapperInfo() != null ? wrapper.getWrapperInfo()
                                                                                                  .getAvailableProcessors() + NetworkUtils.EMPTY_STRING : "0"),
                                       "CPU Usage: " + wrapper.getCpuUsage(),
                                       NetworkUtils.SPACE_STRING);

                }
                break;
                case "sg": {
                    ServerGroup group = CloudNet.getInstance().getServerGroup(parsedLine.words().get(2));
                    if (group == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "Group: " + group.getName(),
                                       "GroupMode:" + group.getGroupMode().name(),
                                       "ServerType: " + group.getServerType().name(),
                                       "JoinPower: " + group.getJoinPower(),
                                       "MaxHeapSize: " + group.getMemory() + "MB",
                                       "MinOnlineServers: " + group.getMinOnlineServers(),
                                       "MaxOnlineServers: " + group.getMaxOnlineServers(),
                                       "Wrappers: " + Arrays.toString(group.getWrapper().toArray(EMPTY_STRING_ARRAY)),
                                       group.getTemplates().stream()
                                            .map(template -> template.getName() + ':' + template.getBackend().name())
                                            .collect(Collectors.joining(", ", "Templates: ", "")),
                                       NetworkUtils.SPACE_STRING);
                }
                break;
            }
        } else {
            sender.sendMessage("info SERVER <server> | show all server informations about one Minecraft server",
                               "info PROXY <proxy> | show all proxy stats from a current BungeeCord",
                               "info WRAPPER <wrapper-id> | show all wrapper properties and stats",
                               "info SG <serverGroup> | show all properties which you set in the group");
        }
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        if (parsedLine.words().size() == 1 && parsedLine.words().get(0).equalsIgnoreCase("info")) {
            candidates.add(new Candidate("server", "Server", null, "show all server informations about one Minecraft server", null,null, true));
            candidates.add(new Candidate("proxy", "Proxy", null, "show all proxy stats from a current BungeeCord", null,null, true));
            candidates.add(new Candidate("wrapper", "Wrapper", null, "show all wrapper properties and stats", null,null, true));
            candidates.add(new Candidate("sg", "Server Group", null, "show all properties which you set in the group", null,null, true));
        }
        if (parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("SERVER")) {
            for (MinecraftServer server : CloudNet.getInstance().getServers().values()) {
                candidates.add(new Candidate(server.getName(), server.getName(), server.getGroup().getName(), "A server", null, null,true));
            }
        }
        if (parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("proxy")) {
            for (ProxyServer server : CloudNet.getInstance().getProxys().values()) {
                candidates.add(new Candidate(server.getName(), server.getName(), server.getProcessMeta().getProxyGroupName(), "A server", null, null,true));
            }
        }
        if (parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("wrapper")) {
            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                candidates.add(new Candidate(wrapper.getName(), wrapper.getName(), null, "A wrapper", null, null,true));
            }
        }
        if (parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("sg")) {
            for (String group : CloudNet.getInstance().getServerGroups().keySet()) {
                candidates.add(new Candidate(group, group, null, "A server group", null, null,true));
            }
        }
        return candidates;
    }
}
