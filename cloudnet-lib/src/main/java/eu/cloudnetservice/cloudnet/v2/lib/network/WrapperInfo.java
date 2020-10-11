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

package eu.cloudnetservice.cloudnet.v2.lib.network;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Objects;

public class WrapperInfo {

    public static final Type TYPE = TypeToken.get(WrapperInfo.class).getType();

    private final String serverId;
    private final String hostName;
    private final String version;
    private final boolean ready;
    private final int availableProcessors;
    private final int startPort;
    private final int processQueueSize;
    private final int memory;

    public WrapperInfo(String serverId,
                       String hostName,
                       String version,
                       boolean ready,
                       int availableProcessors,
                       int startPort,
                       int processQueueSize,
                       int memory) {
        this.serverId = serverId;
        this.hostName = hostName;
        this.version = version;
        this.ready = ready;
        this.availableProcessors = availableProcessors;
        this.startPort = startPort;
        this.processQueueSize = processQueueSize;
        this.memory = memory;
    }

    @Override
    public int hashCode() {
        int result = serverId != null ? serverId.hashCode() : 0;
        result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (ready ? 1 : 0);
        result = 31 * result + availableProcessors;
        result = 31 * result + startPort;
        result = 31 * result + processQueueSize;
        result = 31 * result + memory;
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

        final WrapperInfo that = (WrapperInfo) o;

        if (ready != that.ready) {
            return false;
        }
        if (availableProcessors != that.availableProcessors) {
            return false;
        }
        if (startPort != that.startPort) {
            return false;
        }
        if (processQueueSize != that.processQueueSize) {
            return false;
        }
        if (memory != that.memory) {
            return false;
        }
        if (!Objects.equals(serverId, that.serverId)) {
            return false;
        }
        if (!Objects.equals(hostName, that.hostName)) {
            return false;
        }
        return Objects.equals(version, that.version);
    }

    @Override
    public String toString() {
        return "WrapperInfo{" +
            "serverId='" + serverId + '\'' +
            ", hostName='" + hostName + '\'' +
            ", version='" + version + '\'' +
            ", ready=" + ready +
            ", availableProcessors=" + availableProcessors +
            ", startPort=" + startPort +
            ", process_queue_size=" + processQueueSize +
            ", memory=" + memory +
            '}';
    }

    public String getServerId() {
        return serverId;
    }

    public int getStartPort() {
        return startPort;
    }

    public int getProcessQueueSize() {
        return processQueueSize;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public int getMemory() {
        return memory;
    }

    public String getHostName() {
        return hostName;
    }

    public String getVersion() {
        return version;
    }

    public boolean isReady() {
        return ready;
    }
}
