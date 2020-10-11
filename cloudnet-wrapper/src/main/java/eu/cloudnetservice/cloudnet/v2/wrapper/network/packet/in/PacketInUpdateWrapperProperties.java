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

package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.Type;

public class PacketInUpdateWrapperProperties implements PacketInHandler {

    private static final Type CONFIGURATION_TYPE = TypeToken.get(Configuration.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        Configuration configuration = packet.getData().getObject("configuration", CONFIGURATION_TYPE);

        // Merge configurations
        final Configuration wrapperConfig = CloudNetWrapper.getInstance().getWrapperConfig().getConfiguration();
        configuration.getKeys().forEach(key -> {
            wrapperConfig.set(key, configuration.get(key));
        });

        CloudNetWrapper.getInstance().getWrapperConfig().save();
        CloudNetWrapper.getInstance().getWrapperConfig().load();
    }
}
