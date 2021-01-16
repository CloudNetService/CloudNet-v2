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

package eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.IProtocol;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolStream;

import java.util.Collection;
import java.util.Collections;

public class PacketProtocol implements IProtocol {

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Collection<Class<?>> getAvailableClasses() {
        return Collections.singletonList(Packet.class);
    }

    @Override
    public ProtocolStream createElement(Object element) {
        if (element instanceof Packet) {
            return (Packet) element;
        }
        return null;
    }

    @Override
    public ProtocolStream createEmptyElement() {
        return new Packet();
    }
}
