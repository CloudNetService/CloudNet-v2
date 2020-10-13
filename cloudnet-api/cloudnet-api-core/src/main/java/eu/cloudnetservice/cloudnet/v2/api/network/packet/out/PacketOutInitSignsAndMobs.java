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

package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.mob.MobConfig;
import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign.Sign;
import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign.SignLayoutConfig;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;

public class PacketOutInitSignsAndMobs extends Packet {

    public PacketOutInitSignsAndMobs(SignLayoutConfig signLayoutConfig, MobConfig mobConfig, Map<UUID, Sign> signs) {
        super(PacketRC.CN_CORE + 1, new Document("signLayout", signLayoutConfig).append("mobConfig", mobConfig).append("signs", signs));
    }
}
