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

package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutUpdateWrapperInfo;

public final class CommandReload extends Command {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        CloudNetWrapper.getInstance().getWrapperConfig().load();
        CloudNetWrapper.getInstance().setMaxMemory(CloudNetWrapper.getInstance().getWrapperConfig().getMaxMemory());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutUpdateWrapperInfo());
        System.out.println("Reloading was completed successfully");
    }
}
