package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
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
        if (parsedLine.words().size() <= 1) {
            sender.sendMessage(
                "screen server (-s) | proxy (-p) <name> | The output of the console of the service is transferred to the console of this instance",
                "screen <name> | The output of the console of the service is transferred to the console of this instance",
                "screen leave | The console output closes",
                "screen write <command> | You write a command directly into the console of the service");
            return;
        }
        String commandArgument = parsedLine.words().get(1);
        if (parsedLine.words().size() >= 3 && commandArgument.equalsIgnoreCase("write") && CloudNet.getInstance().getScreenProvider().getMainServiceId() != null) {
            ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
            String commandLine = String.join(" ", parsedLine.words().subList(2,parsedLine.words().size()));
            Wrapper wrapper = CloudNet.getInstance().getWrappers().get(serviceId.getWrapperId());
            if (wrapper != null) {
                if (wrapper.getServers().containsKey(serviceId.getServerId())) {
                    wrapper.writeServerCommand(commandLine, wrapper.getServers().get(serviceId.getServerId()).getServerInfo());
                }
                if (wrapper.getProxies().containsKey(serviceId.getServerId())) {
                    wrapper.writeProxyCommand(commandLine, wrapper.getProxies().get(serviceId.getServerId()).getProxyInfo());
                }
                return;
            }
        }
        switch (parsedLine.words().size()) {
            case 2:
                if (commandArgument.equalsIgnoreCase("leave") && CloudNet.getInstance().getScreenProvider().getMainServiceId() != null) {

                    ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                    CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                    CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                    sender.sendMessage("§aYou left the screen session");
                    return;
                } else {
                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(commandArgument);
                    if (minecraftServer != null) {

                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
                        sender.sendMessage("§aYou joined the screen session of " + minecraftServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    } else {
                        ProxyServer proxyServer = CloudNet.getInstance().getProxy(commandArgument);
                        if (proxyServer != null) {
                            ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                            if (serviceId != null) {
                                CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                                CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                            }

                            proxyServer.getWrapper().enableScreen(proxyServer.getProxyInfo());
                            sender.sendMessage("§aYou joined the screen session of " + proxyServer.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(proxyServer.getServiceId());
                        }
                    }
                }

                break;
            case 3:
                String secondCommandArgument = parsedLine.words().get(2);
                if (commandArgument.equalsIgnoreCase("-s") || commandArgument.equalsIgnoreCase("server")) {

                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(secondCommandArgument);
                    if (minecraftServer != null) {

                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
                        sender.sendMessage("§aYou joined the screen session of " + minecraftServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                } else
                if (commandArgument.equalsIgnoreCase("-p") || commandArgument.equalsIgnoreCase("proxy")) {

                    ProxyServer proxyServer = CloudNet.getInstance().getProxy(secondCommandArgument);
                    if (proxyServer != null) {
                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        proxyServer.getWrapper().enableScreen(proxyServer.getProxyInfo());
                        sender.sendMessage("§aYou joined the screen session of " + proxyServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(proxyServer.getServiceId());
                    }
                    return;
                }
                break;
            default:
                sender.sendMessage(
                    "screen server (-s) | proxy (-p) <name> | The output of the console of the service is transferred to the console of this instance",
                    "screen <name> | The output of the console of the service is transferred to the console of this instance",
                    "screen leave | The console output closes",
                    "screen write <command> | You write a command directly into the console of the service");
                break;
        }
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> strings = new ArrayList<>();
        if (parsedLine.words().size() >= 1) {
            if (parsedLine.words().get(0).equalsIgnoreCase("screen")) {
                if (parsedLine.wordIndex() >= 2) {
                    String commandArgument = parsedLine.words().get(1);
                    if (commandArgument.equalsIgnoreCase("server")  || commandArgument.equalsIgnoreCase("-s")) {
                        strings.addAll(CloudNet.getInstance().getServers().values().stream().map(minecraftServer -> new Candidate(minecraftServer.getName(), minecraftServer.getName(), minecraftServer.getGroup().getName(), "A simple minecraft server", null, null, true)).collect(
                            Collectors.toList()));
                        return strings;
                    }
                    if (commandArgument.equalsIgnoreCase("proxy")  || commandArgument.equalsIgnoreCase("-p")) {
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
                strings.addAll(CloudNet.getInstance().getServers().values().stream().map(minecraftServer -> new Candidate(minecraftServer.getName(), minecraftServer.getName(), minecraftServer.getGroup().getName(), "A simple minecraft server", null, null, true)).collect(
                    Collectors.toList()));
                strings.addAll(CloudNet.getInstance().getProxys().values().stream().map(proxyServer -> new Candidate(proxyServer.getName(), proxyServer.getName(), proxyServer.getProcessMeta().getProxyGroupName(), "A simple proxy", null, null, true)).collect(
                    Collectors.toList()));
                return strings;
            }

        }
        return strings;
    }
}
