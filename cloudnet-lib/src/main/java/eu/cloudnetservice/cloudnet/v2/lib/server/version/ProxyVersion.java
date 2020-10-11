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

package eu.cloudnetservice.cloudnet.v2.lib.server.version;

import eu.cloudnetservice.cloudnet.v2.lib.MultiValue;

public enum ProxyVersion {

    TRAVERTINE,
    BUNGEECORD,
    WATERFALL,
    HEXACORD;

    public static MultiValue<String, String> url(ProxyVersion proxyVersion) {
        switch (proxyVersion) {
            case TRAVERTINE:
                return new MultiValue<>(
                    "https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar",
                    "Travertine.jar");
            case HEXACORD:
                return new MultiValue<>("https://github.com/HexagonMC/BungeeCord/releases/download/v258/BungeeCord.jar",
                                        "HexaCord.jar");
            case WATERFALL:
                return new MultiValue<>(
                    "https://ci.destroystokyo.com/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar",
                    "Waterfall.jar");
            default:
                return new MultiValue<>("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar",
                                        "BungeeCord.jar");
        }
    }

}
