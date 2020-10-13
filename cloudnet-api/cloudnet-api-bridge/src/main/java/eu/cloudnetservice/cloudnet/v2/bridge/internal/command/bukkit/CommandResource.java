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

package eu.cloudnetservice.cloudnet.v2.bridge.internal.command.bukkit;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.bridge.CloudServer;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public final class CommandResource extends Command {

    public CommandResource() {
        super("resource");
        setPermission("cloudnet.command.resource");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s with arguments %s", sender, alias, Arrays.toString(args)));
        if (!testPermission(sender)) {
            return false;
        }
        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
        long max = Runtime.getRuntime().maxMemory() / 1048576L;

        sender.sendMessage(CloudAPI.getInstance().getPrefix() + NetworkUtils.SPACE_STRING);
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Server: §b" + CloudAPI.getInstance()
                                                                                         .getServerId() + ':' + CloudAPI.getInstance()
                                                                                                                        .getUniqueId());
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7State§8: §b" + CloudServer.getInstance().getServerState());
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Template: §b" + CloudServer.getInstance().getTemplate().getName());
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Memory: §b" + used + "§7/§b" + max + "MB");
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7CPU-Usage internal: §b" + NetworkUtils.DECIMAL_FORMAT.format(NetworkUtils
                                                                                                                                    .internalCpuUsage()));
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + NetworkUtils.SPACE_STRING);
        return false;
    }
}
