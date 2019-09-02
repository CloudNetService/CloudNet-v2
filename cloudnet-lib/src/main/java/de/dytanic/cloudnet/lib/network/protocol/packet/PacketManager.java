/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.Value;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 22.05.2017.
 */
public final class PacketManager {

    private final java.util.Map<Integer, Collection<Class<? extends PacketInHandler>>> packetHandlers = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<UUID, Value<Result>> synchronizedHandlers = NetworkUtils.newConcurrentHashMap();
    private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();
    private final TaskScheduler executorService = new TaskScheduler(1);

    public void registerHandler(int id, Class<? extends PacketInHandler> packetHandlerClass) {
        if (!packetHandlers.containsKey(id)) {
            packetHandlers.put(id, new ArrayList<>());
        }

        packetHandlers.get(id).add(packetHandlerClass);
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
        Value<Result> handled = new Value<>(null);
        synchronizedHandlers.put(uniqueId, handled);
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                packetSender.sendPacket(packet);
            }
        });

        int i = 0;

        while (synchronizedHandlers.get(uniqueId).getValue() == null && i++ < 5000) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException ignored) {
            }
        }

        if (i >= 4999) {
            synchronizedHandlers.get(uniqueId).setValue(new Result(uniqueId, new Document()));
        }

        Value<Result> values = synchronizedHandlers.get(uniqueId);
        synchronizedHandlers.remove(uniqueId);
        return values.getValue();
    }

    public boolean dispatchPacket(Packet incoming, PacketSender packetSender) {
        if (incoming.uniqueId != null && synchronizedHandlers.containsKey(incoming.uniqueId)) {
            Result result = new Result(incoming.uniqueId, incoming.data);
            Value<Result> x = synchronizedHandlers.get(incoming.uniqueId);
            x.setValue(result);
            return false;
        }

        Collection<PacketInHandler> handlers = buildHandlers(incoming.id);
        CollectionWrapper.iterator(handlers, new Runnabled<PacketInHandler>() {
            @Override
            public void run(PacketInHandler handler) {
                if (incoming.uniqueId != null) {
                    handler.packetUniqueId = incoming.uniqueId;
                }
                if (handler != null) {
                    handler.handleInput(incoming.data, packetSender);
                }
            }
        });
        return true;
    }

    public Collection<PacketInHandler> buildHandlers(int id) {
        Collection<PacketInHandler> packetIn = new LinkedList<>();
        if (packetHandlers.containsKey(id)) {
            for (Class<? extends PacketInHandler> handlers : packetHandlers.get(id)) {
                try {
                    packetIn.add(handlers.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    return null;
                }
            }
        }
        return packetIn;
    }

    public UUID uniqueId(Packet packet) {
        return packet.uniqueId;
    }

    public PacketManager injectUniqueId(Packet packet, UUID uniqueId) {
        packet.uniqueId = uniqueId;
        return this;
    }

    public int packetId(Packet packet) {
        return packet.id;
    }

    public Document packetData(Packet packet) {
        return packet.data;
    }

}
