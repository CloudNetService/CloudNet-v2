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

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.result.Result;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

public final class PacketManager {

    private final Map<Integer, List<PacketInHandler>> packetHandlers = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<Result>> synchronizedHandlers = new ConcurrentHashMap<>();
    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();

    public void registerHandler(int id, Class<? extends PacketInHandler> packetHandlerClass) {
        try {
            registerHandler(id, packetHandlerClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void registerHandler(int id, PacketInHandler packetHandler) {
        if (!packetHandlers.containsKey(id)) {
            packetHandlers.put(id, new ArrayList<>());
        }
        packetHandlers.get(id).add(packetHandler);
    }

    public void clearHandlers() {
        packetHandlers.clear();
    }

    public PacketManager queuePacket(Packet packet) {
        this.packetQueue.offer(packet);
        return this;
    }

    public PacketManager dispatchQueue(PacketSender packetSender) {
        while (!this.packetQueue.isEmpty()) {
            packetSender.sendPacket(this.packetQueue.remove());
        }

        return this;
    }

    public Result sendQuery(Packet packet, PacketSender packetSender) {
        UUID uniqueId = UUID.randomUUID();
        packet.uniqueId = uniqueId;
        CompletableFuture<Result> future = new CompletableFuture<>();
        synchronizedHandlers.put(uniqueId, future);
        NetworkUtils.getExecutor().submit(() -> packetSender.sendPacket(packet));

        try {
            final Result result = future.get(2, TimeUnit.SECONDS);
            synchronizedHandlers.remove(uniqueId);
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return new Result(uniqueId, new Document());
        }
    }

    public boolean dispatchPacket(Packet incoming, PacketSender packetSender) {
        // Query future present, resolve it and skip any other handlers
        if (incoming.uniqueId != null && synchronizedHandlers.containsKey(incoming.uniqueId)) {
            Result result = new Result(incoming.uniqueId, incoming.data);
            synchronizedHandlers.get(incoming.uniqueId).complete(result);
            return false;
        }

        packetHandlers.getOrDefault(incoming.id, Collections.emptyList())
                      .forEach(handler -> {
                          handler.handleInput(incoming, packetSender);
                      });
        return true;
    }

    public Collection<PacketInHandler> buildHandlers(int id) {
        return packetHandlers.getOrDefault(id, Collections.emptyList());
    }

}
