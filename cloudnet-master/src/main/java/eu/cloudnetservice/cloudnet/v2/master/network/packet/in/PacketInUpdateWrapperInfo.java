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

package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.WrapperInfo;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

public final class PacketInUpdateWrapperInfo implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        WrapperInfo wrapperInfo = packet.getData().getObject("wrapperInfo", WrapperInfo.TYPE);

        Wrapper wrapper = (Wrapper) packetSender;
        if (wrapper.getWrapperInfo() != null) {
            wrapper.setWrapperInfo(wrapperInfo);
            wrapper.setMaxMemory(wrapperInfo.getMemory());
            System.out.println("Wrapper [" + wrapper.getServerId() + "] is ready with C" + wrapperInfo.getAvailableProcessors() +
                                   " and " + wrapperInfo.getMemory() + "MB");
        } else {
            wrapper.setWrapperInfo(wrapperInfo);
            wrapper.setMaxMemory(wrapperInfo.getMemory());
            wrapper.updateWrapper();
            System.out.println("Wrapper [" + wrapper.getServerId() + "] is ready with C" + wrapperInfo.getAvailableProcessors() +
                                   " and " + wrapperInfo.getMemory() + "MB");

            if (wrapperInfo.getVersion() != null && !wrapperInfo.getVersion().equals(
                NetworkUtils.class.getPackage().getImplementationVersion())) {
                System.err.println("Wrapper [" + wrapper.getServerId() + "] does not use the same version as this CloudNet Master [Master:"
                                       + NetworkUtils.class.getPackage().getImplementationVersion() +
                                       "/Wrapper:" + wrapperInfo.getVersion() + "], please update");
            }
        }
    }
}
