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

package eu.cloudnetservice.cloudnet.v2.lib.server;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.Objects;

public class ServerConfig {

    public static final Type TYPE = TypeToken.get(ServerConfig.class).getType();

    private boolean hideServer;
    private Document properties;
    private long startup;

    public ServerConfig() {
        this.hideServer = false;
        this.properties = new Document();
        this.startup = System.currentTimeMillis();
    }

    public ServerConfig(boolean hideServer, Document properties, long startup) {
        this.hideServer = hideServer;
        this.properties = properties;
        this.startup = startup;
    }

    @Override
    public int hashCode() {
        int result = (hideServer ? 1 : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (int) (startup ^ (startup >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServerConfig that = (ServerConfig) o;

        if (hideServer != that.hideServer) {
            return false;
        }
        if (startup != that.startup) {
            return false;
        }
        return Objects.equals(properties, that.properties);
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
            "hideServer=" + hideServer +
            ", properties=" + properties +
            ", startup=" + startup +
            '}';
    }

    public Document getProperties() {
        return properties;
    }

    public void setProperties(Document properties) {
        this.properties = properties;
    }

    public long getStartup() {
        return startup;
    }

    public void setStartup(final long startup) {
        this.startup = startup;
    }

    public boolean isHideServer() {
        return hideServer;
    }

    public void setHideServer(boolean hideServer) {
        this.hideServer = hideServer;
    }
}
