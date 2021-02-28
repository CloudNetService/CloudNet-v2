package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandScreen extends Command implements TabCompletable {

    public CommandScreen() {
        super("screen", "cloudnet.command.screen", "sc");

        description = "Shows you the console of one server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {

        if (CloudNet.getInstance().getScreenProvider().getMainServiceId() != null && parsedLine.words().size() > 2 && parsedLine.words().get(1).equalsIgnoreCase("write")) {
            ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < parsedLine.words().size(); i++) {
                stringBuilder.append(parsedLine.words().get(i)).append(NetworkUtils.SPACE_STRING);
            }
            String commandLine = stringBuilder.substring(0, stringBuilder.length() - 1);
            Wrapper wrapper = CloudNet.getInstance().getWrappers().get(serviceId.getWrapperId());
            if (wrapper != null) {
                if (wrapper.getServers().containsKey(serviceId.getServerId())) {
                    wrapper.writeServerCommand(commandLine, wrapper.getServers().get(serviceId.getServerId()).getServerInfo());
                }
                if (wrapper.getProxies().containsKey(serviceId.getServerId())) {
                    wrapper.writeProxyCommand(commandLine, wrapper.getProxies().get(serviceId.getServerId()).getProxyInfo());
                }
            }
            return;
        }

        switch (parsedLine.words().size()) {
            case 2:
                if (parsedLine.words().get(1).equalsIgnoreCase("leave") && CloudNet.getInstance().getScreenProvider().getMainServiceId() != null) {

                    ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                    CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                    CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                    sender.sendMessage("You left the screen session");
                    return;
                }
                break;
            case 3:
                if (parsedLine.words().get(1).equalsIgnoreCase("-s") || parsedLine.words().get(1).equalsIgnoreCase("server")) {

                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(parsedLine.words().get(2));
                    if (minecraftServer != null) {

                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
                        sender.sendMessage("You joined the screen session of " + minecraftServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("-p") || parsedLine.words().get(1).equalsIgnoreCase("proxy")) {

                    ProxyServer minecraftServer = CloudNet.getInstance().getProxy(parsedLine.words().get(2));
                    if (minecraftServer != null) {
                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getProxyInfo());
                        sender.sendMessage("You joined the screen session of " + minecraftServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                }
                break;
            default:
                sender.sendMessage(
                    "screen server (-s) | proxy (-p) <name> | The output of the console of the service is transferred to the console of this instance",
                    "screen leave | The console output closes",
                    "screen write <command> | You write a command directly into the console of the service");
                break;
        }
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> strings = new ArrayList<>();
        if (parsedLine.words().size() > 1) {

            if (parsedLine.words().get(1).equalsIgnoreCase("screen")) {
                if (parsedLine.wordIndex() > 2) {
                    if (parsedLine.words().get(2).equalsIgnoreCase("server")  || parsedLine.words().get(2).equalsIgnoreCase("-s")) {
                        strings.addAll(CloudNet.getInstance().getServers().values().stream().map(minecraftServer -> new Candidate(minecraftServer.getName(), minecraftServer.getName(), minecraftServer.getGroup().getName(), "A simple minecraft server", null, null, true)).collect(
                            Collectors.toList()));
                        return strings;
                    }
                    if (parsedLine.words().get(2).equalsIgnoreCase("proxy")  || parsedLine.words().get(2).equalsIgnoreCase("-p")) {
                        strings.addAll(CloudNet.getInstance().getProxys().values().stream().map(proxyServer -> new Candidate(proxyServer.getName(), proxyServer.getName(), proxyServer.getProcessMeta().getProxyGroupName(), "A simple proxy", null, null, true)).collect(
                            Collectors.toList()));
                        return strings;

                    }
                }
                strings.add(new Candidate("write", "write", null, "Write a command into the open screen", null, null, true));
                strings.add(new Candidate("server", "server", "screen-server", "Open a server screen", null, null,true));
                strings.add(new Candidate("-s", "-s", "screen-server", "Open a server screen", null, null,true));
                strings.add(new Candidate("proxy", "proxy", "screen-proxy", "Open a proxy screen", null, null,true));
                strings.add(new Candidate("-p", "-p", "screen-proxy", "Open a proxy screen", null, null,true));
                strings.add(new Candidate("leave", "leave", null, "Close/leave the current screen", null, null,true));
                return strings;
            }

        }
        return strings;
    }
}
