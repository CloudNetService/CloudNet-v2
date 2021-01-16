/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

public final class CommandCmd extends Command {

    public CommandCmd() {
        super("cmd", "cloudnet.command.cmd", "command");

        description = "Executes a command on a game server or proxy server";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
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

                for (ProxyServer proxyServer : wrapper.getProxies().values()) {
                    if (proxyServer.getServiceId().getServerId().equalsIgnoreCase(args[0])) {
                        proxyServer.getWrapper().writeProxyCommand(command, proxyServer.getProxyInfo());
                        sender.sendMessage("Sending command to " + proxyServer.getServiceId()
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
