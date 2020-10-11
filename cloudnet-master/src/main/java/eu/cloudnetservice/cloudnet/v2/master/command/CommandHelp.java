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

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHelp extends Command {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public CommandHelp() {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        List<String> messages = new ArrayList<>(CloudNet.getInstance().getCommandManager().getCommands().size() + 9);

        for (String command : CloudNet.getInstance().getCommandManager().getCommands()) {
            messages.add(command + " | " + CloudNet.getInstance().getCommandManager().getCommand(command).getDescription());
        }

        messages.add(NetworkUtils.SPACE_STRING);
        messages.add("Server groups:");
        messages.add(Arrays.toString(CloudNet.getInstance().getServerGroups().keySet().toArray(EMPTY_STRING_ARRAY)));
        messages.add("Proxy groups:");
        messages.add(Arrays.toString(CloudNet.getInstance().getProxyGroups().keySet().toArray(EMPTY_STRING_ARRAY)));
        messages.add(NetworkUtils.SPACE_STRING);
        messages.add(String.format("The Cloud uses %d/%dMB",
                                   ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L,
                                   ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L));
        messages.add("CPU on this instance " + new DecimalFormat("##.##").format(NetworkUtils.internalCpuUsage()) + "/100 %");
        messages.add(NetworkUtils.SPACE_STRING);

        sender.sendMessage(messages.toArray(EMPTY_STRING_ARRAY));
    }
}
