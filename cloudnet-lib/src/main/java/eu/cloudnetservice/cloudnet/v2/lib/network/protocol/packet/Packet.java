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

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolBuffer;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolStream;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Packet objective
 */
public class Packet extends ProtocolStream {

    private static final Type TYPE = TypeToken.get(Packet.class).getType();

    public int getId() {
        return id;
    }

    public Document getData() {
        return data;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    protected int id;
    protected Document data;
    protected UUID uniqueId;

    @Override
    public String toString() {
        return "Packet{" +
            "id=" + id +
            ", data=" + data +
            ", uniqueId=" + uniqueId +
            "} " + super.toString();
    }

    public Packet() {
    }

    public Packet(int id) {
        this.id = id;
        this.data = new Document();
    }

    public Packet(Document data) {
        this.data = data;
        this.id = 0;
    }

    public Packet(int id, Document data) {
        this.id = id;
        this.data = data;
    }

    public Packet(UUID uniqueId, int id, Document data) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.data = data;
    }

    @Override
    public void write(ProtocolBuffer outPut) throws Exception {
        outPut.writeString(NetworkUtils.GSON.toJson(this));
    }

    @Override
    public void read(ProtocolBuffer in) throws Exception {
        int vx = in.readableBytes();
        if (vx != 0) {
            String input = in.readString();
            Packet packet = NetworkUtils.GSON.fromJson(input, TYPE);
            this.uniqueId = packet.uniqueId;
            this.data = packet.data;
            this.id = packet.id;
        }
    }
}
