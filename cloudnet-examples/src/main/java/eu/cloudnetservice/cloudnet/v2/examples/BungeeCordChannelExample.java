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

package eu.cloudnetservice.cloudnet.v2.examples;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BungeeCordChannelExample {

    public void sendToaRandomServerInGroup(Plugin plugin, Player player) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "cloudnet:main");
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Connect");
        byteArrayDataOutput.writeUTF("Lobby"); //Connect to the group Lobby
        player.sendPluginMessage(plugin, "cloudnet:main", byteArrayDataOutput.toByteArray());
    }

    public void sendToFallback(Plugin plugin, Player player) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "cloudnet:main");
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Fallback"); //Connect to the fallback server in the iteration
        player.sendPluginMessage(plugin, "cloudnet:main", byteArrayDataOutput.toByteArray());
    }

}
