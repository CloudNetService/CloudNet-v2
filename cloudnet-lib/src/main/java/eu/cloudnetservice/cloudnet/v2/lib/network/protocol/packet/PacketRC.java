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

public final class PacketRC {

    public static final int INTERNAL = 0;
    public static final int CN_WRAPPER = 100;
    public static final int CN_CORE = 200;
    public static final int PLAYER_HANDLE = 300;
    public static final int SERVER_HANDLE = 400;
    public static final int SERVER_SELECTORS = 500;
    public static final int DB = 600;
    public static final int CN_INTERNAL_CHANNELS = 700;
    public static final int API = 800;
    public static final int MODULE = 10000;

    private PacketRC() {
    }

}
