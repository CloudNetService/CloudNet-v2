package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnetcore.CloudNet;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartProxy implements PacketInHandler {

    private static final Type STRING_ARRAY_TYPE = TypeToken.getArray(String.class).getType();
    private static final Type COLLECTION_SERVER_INSTALLABLE_PLUGIN_TYPE = TypeToken.getParameterized(Collection.class,
                                                                                                     ServerInstallablePlugin.class)
                                                                                   .getType();
    private static final Type PROPERTIES_TYPE = TypeToken.get(Properties.class).getType();


    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("wrapper")) {
            CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(packet.getData().getString("group")),
                                              packet.getData().getInt("memory"),
                                              packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                              packet.getData().getString("url"),
                                              packet.getData()
                                                    .getObject("plugins", COLLECTION_SERVER_INSTALLABLE_PLUGIN_TYPE),
                                              packet.getData().getDocument("properties"));
        } else {
            CloudNet.getInstance().startProxy(CloudNet.getInstance().getWrappers().get(packet.getData().getString("wrapper")),
                                              CloudNet.getInstance().getProxyGroups().get(packet.getData().getString("group")),
                                              packet.getData().getInt("memory"),
                                              packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                              packet.getData().getString("url"),
                                              packet.getData()
                                                    .getObject("plugins", COLLECTION_SERVER_INSTALLABLE_PLUGIN_TYPE),
                                              packet.getData().getDocument("properties"));
        }
    }
}
