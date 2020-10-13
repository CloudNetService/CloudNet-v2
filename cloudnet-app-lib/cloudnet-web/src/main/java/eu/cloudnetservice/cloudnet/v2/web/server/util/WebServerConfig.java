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

package eu.cloudnetservice.cloudnet.v2.web.server.util;

/**
 * Configuration class for the web server
 */
public class WebServerConfig {

    /**
     * Whether the web server is enabled or not
     */
    private final boolean enabled;

    /**
     * The address the web server is bound to
     */
    private final String address;

    /**
     * Port that this web server is bound to
     */
    private final int port;

    public WebServerConfig(boolean enabled, String address, int port) {
        this.enabled = enabled;
        this.address = address;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
