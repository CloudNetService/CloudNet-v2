package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.ParsedLine;

public final class CommandList extends Command {

    public CommandList() {
        super("list", "cloudnet.command.list");

        description = "Lists some information of the network";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        sender.sendMessage("CloudNet: ");
        sender.sendMessage("Wrappers: ");
        int memory = 0, maxMemory = 0;
        for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
            memory += wrapper.getUsedMemory();
            maxMemory += wrapper.getMaxMemory();
            String stringBuilder = " - " + wrapper.getServerId() + "@" + wrapper.getNetworkInfo()
                                                                                .getHostName() + "(" + colorizeUsage((int) wrapper.getCpuUsage(), 100) + "% | Memory: " + colorizeUsage(wrapper.getUsedMemory(),
                                                                            wrapper.getMaxMemory()) + "MB | " + (wrapper.getWrapperInfo() != null ? wrapper
                .getWrapperInfo()
                .getVersion() : "Unknown") + ")";
            sender.sendMessage(stringBuilder);
            sender.sendMessage(" ", "Proxys:");
            for (ProxyServer proxyServer : wrapper.getProxies().values()) {
                sender.sendMessage(" - " + proxyServer.getServerId() + "@" + proxyServer.getNetworkInfo()
                                                                                              .getHostName() + " | " + colorizeUsage(proxyServer.getProxyInfo()
                                                                                                                                  .getOnlineCount(), CloudNet
                    .getInstance()
                    .getProxyGroups()
                    .get(proxyServer.getProxyInfo().getServiceId().getGroup())
                    .getProxyConfig()
                    .getMaxPlayers()) + " | State: " + (proxyServer.getChannel() != null ? "connected" : "not connected"));
            }
            sender.sendMessage(" ", "Servers:");
            for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                sender.sendMessage(" - " + minecraftServer.getServerId() + "@" + minecraftServer.getServerInfo()
                                                                                               .getHost() + " | " + colorizeUsage(minecraftServer.getServerInfo()
                                                                                                                                             .getOnlineCount(), minecraftServer
                    .getServerInfo()
                    .getMaxPlayers()) + " | State: " + (minecraftServer.getChannel() != null ? "connected" : "not connected"));
            }
            sender.sendMessage(" ");
        }
        sender.sendMessage(" ", "Online Players:");
        for (CloudPlayer cloudPlayer : CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values()) {
            sender.sendMessage("* " + cloudPlayer.getUniqueId() + '#' + cloudPlayer.getName() + "@" + cloudPlayer.getProxy() + '>' + cloudPlayer
                .getServer());
        }

        sender.sendMessage(" ");

        sender.sendMessage("CloudNet uses " + colorizeUsage(memory, maxMemory) + "MB ");

    }

    private String colorizeUsage(int used, int max) {
        int onePer = max / 100;
        int fivePer = onePer * 50;
        int eightPer = onePer * 80;

        if (used >= fivePer && used <= eightPer) {
            return "§e" + used + "§r" + NetworkUtils.SLASH_STRING + max;
        } else if (used >= fivePer && used >= eightPer) {
            return "§c" + used + "§r" + NetworkUtils.SLASH_STRING + max;
        }
        return "§a" + used + "§r" + NetworkUtils.SLASH_STRING + max;
    }
}
