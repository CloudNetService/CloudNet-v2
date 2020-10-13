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
import eu.cloudnetservice.cloudnet.v2.master.module.CloudModule;

public final class CommandModules extends Command {

    public CommandModules() {
        super("modules", "cloudnet.cowmmand.modules", "m");

        description = "Lists all modules, versions and authors";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Running modules:", NetworkUtils.SPACE_STRING);
        for (CloudModule module : CloudNet.getInstance().getModuleManager().getModules().values()) {
            sender.sendMessage(module.getModuleJson().getName() + ' ' + module.getModuleJson()
                                                                              .getVersion() + " by " + module.getModuleJson()
                                                                                                             .getAuthorsAsString() + NetworkUtils.EMPTY_STRING);
        }
    }
}
