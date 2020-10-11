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

package eu.cloudnetservice.cloudnet.v2.master.network.wrapper;

import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;

import java.io.Serializable;
import java.util.UUID;

public class WrapperSession implements Serializable {

    private final UUID uniqueId;

    private final WrapperMeta wrapperMeta;

    private final long connected;

    public WrapperSession(UUID uniqueId, WrapperMeta wrapperMeta, long connected) {
        this.uniqueId = uniqueId;
        this.wrapperMeta = wrapperMeta;
        this.connected = connected;
    }

    public long getConnected() {
        return connected;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public WrapperMeta getWrapperMeta() {
        return wrapperMeta;
    }
}
