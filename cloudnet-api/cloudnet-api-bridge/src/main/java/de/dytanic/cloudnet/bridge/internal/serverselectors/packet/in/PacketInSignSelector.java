/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitUpdateSignLayoutsEvent;
import de.dytanic.cloudnet.bridge.internal.serverselectors.SignSelector;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 23.08.2017.
 */
public class PacketInSignSelector implements PacketInHandler {

    private static final Type MAP_UUID_SIGN_TYPE = TypeToken.getParameterized(Map.class, UUID.class, Sign.class).getType();
    private static final Type SIGN_LAYOUT_CONFIG_TYPE = TypeToken.get(SignLayoutConfig.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        Map<UUID, Sign> signMap = packet.getData().getObject("signs", MAP_UUID_SIGN_TYPE);
        SignLayoutConfig signLayoutConfig = packet.getData().getObject("signLayoutConfig", SIGN_LAYOUT_CONFIG_TYPE);
        final String group = CloudAPI.getInstance().getGroup();

        signMap.entrySet().removeIf(entry -> !entry.getValue().getPosition().getGroup().equals(group));

        Bukkit.getPluginManager().callEvent(new BukkitUpdateSignLayoutsEvent(signLayoutConfig));

        if (SignSelector.getInstance() != null) {
            SignSelector.getInstance().setSignLayoutConfig(signLayoutConfig);

            SignSelector.getInstance().getSigns().clear();

            for (Sign sign : signMap.values()) {
                SignSelector.getInstance().getSigns().put(sign.getUniqueId(), sign);
            }
        } else {
            SignSelector signSelector = new SignSelector(signMap, signLayoutConfig);
            signSelector.start();
        }
    }
}
