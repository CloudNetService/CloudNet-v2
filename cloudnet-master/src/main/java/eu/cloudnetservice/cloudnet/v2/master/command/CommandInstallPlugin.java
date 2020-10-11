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
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;

public final class CommandInstallPlugin extends Command {

    public CommandInstallPlugin() {
        super("installplugin", "cloudnet.command.installplugin");

        description = "Installs plugin onto a server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        switch (args.length) {
            case 3:
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if (minecraftServer != null && minecraftServer.getChannel() != null) {
                    minecraftServer.sendCustomMessage("cloudnet_internal",
                                                      "install_plugin",
                                                      new Document("name", args[1]).append("url", args[2]));
                    sender.sendMessage("Plugin will install on " + args[0] + "...");
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
