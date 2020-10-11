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

package eu.cloudnetservice.cloudnet.v2.lib.server.resource;

public class ResourceMeta {

    private final double cpuUsage; //%

    private final long heapMemory; //KB

    private final long maxHeapMemory; //KB

    public ResourceMeta(double cpuUsage, long heapMemory, long maxHeapMemory) {
        this.cpuUsage = cpuUsage;
        this.heapMemory = heapMemory;
        this.maxHeapMemory = maxHeapMemory;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public long getHeapMemory() {
        return heapMemory;
    }

    public long getMaxHeapMemory() {
        return maxHeapMemory;
    }
}
