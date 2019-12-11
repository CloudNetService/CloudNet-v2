/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Tareko on 22.05.2017.
 */
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
