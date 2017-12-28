/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public class CommandShutdown extends Command {

    public CommandShutdown()
    {
        super("shutdown", "cloudnet.command.shutdown");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
                if (args[0].equalsIgnoreCase("wrapper"))
                {
                    if (CloudNet.getInstance().getWrappers().containsKey(args[1]))
                    {
                        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(args[1]);
                        if (wrapper.getChannel() != null)
                        {
                            wrapper.writeCommand("stop");
                        }
                        sender.sendMessage("Stop wrapper " + args[1]);
                    } else
                    {
                        sender.sendMessage("Wrapper doesn't exists");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("group"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        System.out.println("Stop all servers from group " + args[1]);
                        CollectionWrapper.iterator(CloudNet.getInstance().getServers(args[1]), new Runnabled<MinecraftServer>() {
                            @Override
                            public void run(MinecraftServer obj)
                            {
                                obj.getWrapper().stopServer(obj);
                                NetworkUtils.sleepUninterruptedly(1000);
                            }
                        });
                        return;
                    }
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]))
                    {
                        System.out.println("Stop all servers from group " + args[1]);
                        CollectionWrapper.iterator(CloudNet.getInstance().getProxys(args[1]), new Runnabled<ProxyServer>() {
                            @Override
                            public void run(ProxyServer obj)
                            {
                                obj.getWrapper().stopProxy(obj);
                                NetworkUtils.sleepUninterruptedly(1000);
                            }
                        });
                        return;
                    }

                    sender.sendMessage("Group doesn't exists");
                    return;
                }
                if (args[0].equalsIgnoreCase("server"))
                {
                    MinecraftServer proxyServer = CloudNet.getInstance().getServer(args[1]);
                    if (proxyServer != null)
                    {
                        proxyServer.getWrapper().stopServer(proxyServer);
                        sender.sendMessage("Stop server " + args[1]);
                    } else
                    {
                        CloudServer proxyServers = CloudNet.getInstance().getCloudGameServer(args[1]);
                        if(proxyServers != null)
                        {
                            proxyServers.getWrapper().stopServer(proxyServers);
                        }
                        else
                        {
                            sender.sendMessage("The server doesn't exists");
                        }
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("proxy"))
                {
                    ProxyServer proxyServer = CloudNet.getInstance().getProxy(args[1]);
                    if (proxyServer != null)
                    {
                        proxyServer.getWrapper().stopProxy(proxyServer);
                        sender.sendMessage("Stop proxy server " + args[1]);
                    } else
                    {
                        sender.sendMessage("The proxy doesn't exists");
                    }
                    return;
                }
                break;
            default:
                sender.sendMessage(
                        " ",
                        "shutdown WRAPPER <wrapper-id> | Stops a wrapper service with the respective \"wrapper ID\"",
                        "shutdown GROUP <group-id> | Stops a group of either proxy or servergroup, and restarts it by default",
                        "shutdown PROXY <proxy-id> | Stops a BungeeCord service and after preconfiguring the group, a new one is started",
                        "shutdown SERVER <server-id> | Stops a Minecraft server service and after preconfiguring the group, a new one is started",
                        " "
                );
                break;
        }
    }
}