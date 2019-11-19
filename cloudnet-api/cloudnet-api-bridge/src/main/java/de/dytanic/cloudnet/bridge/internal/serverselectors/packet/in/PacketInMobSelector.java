/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInMobSelector implements PacketInHandlerDefault {

    private static final Type UUID_SERVERMOB_MAP_TYPE = TypeToken.getParameterized(Map.class, UUID.class, ServerMob.class).getType();
    private static final Type MOBCONFIG_TYPE = TypeToken.get(MobConfig.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        Map<UUID, ServerMob> mobMap = packet.getData().getObject("mobs", UUID_SERVERMOB_MAP_TYPE);
        MobConfig mobConfig = packet.getData().getObject("mobConfig", MOBCONFIG_TYPE);
        final String group = CloudAPI.getInstance().getGroup();

        mobMap.entrySet().removeIf(entry -> !entry.getValue().getPosition().getGroup().equals(group));

        if (MobSelector.getInstance() != null) {
            MobSelector.getInstance().shutdown();
            MobSelector.getInstance().setMobConfig(mobConfig);
            MobSelector.getInstance().setMobs(new HashMap<>());
            runBukkitTask(mobMap, mobConfig);

            Bukkit.getScheduler().runTaskAsynchronously(CloudServer.getInstance().getPlugin(), () -> {
                for (ServerInfo serverInfo : MobSelector.getInstance().getServers().values()) {
                    MobSelector.getInstance().handleUpdate(serverInfo);
                }
            });
        } else {
            MobSelector mobSelector = new MobSelector(mobConfig);
            MobSelector.getInstance().setMobs(new HashMap<>());
            runBukkitTask(mobMap, mobConfig);
            mobSelector.init();
        }
    }

    private static void runBukkitTask(final Map<UUID, ServerMob> mobMap, final MobConfig mobConfig) {
        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), () -> {
            Map<UUID, MobSelector.MobImpl> mobImplementationMap = new HashMap<>();

            mobMap.forEach((uuid, serverMob) -> {
                MobSelector.MobImpl mob = MobSelector.getInstance().spawnMob(mobConfig, uuid, serverMob);
                if (mob == null) {
                    return;
                }

                mobImplementationMap.put(uuid, mob);
            });

            MobSelector.getInstance().setMobs(mobImplementationMap);
        });
    }

}
