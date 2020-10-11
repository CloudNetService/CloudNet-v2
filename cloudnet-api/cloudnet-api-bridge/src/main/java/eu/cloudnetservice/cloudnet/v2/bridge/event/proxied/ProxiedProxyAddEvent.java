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

package eu.cloudnetservice.cloudnet.v2.bridge.event.proxied;

import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;

/**
 * This event is called when a new proxy is added to the CloudNet network.
 * The proxy may not be done with its initialization when receiving this event.
 */
public class ProxiedProxyAddEvent extends ProxiedCloudEvent {

    private final ProxyInfo proxyInfo;

    public ProxiedProxyAddEvent(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    /**
     * The proxy information object of the proxy that has been added to the network.
     *
     * @return the information about the newly added proxy.
     */
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
