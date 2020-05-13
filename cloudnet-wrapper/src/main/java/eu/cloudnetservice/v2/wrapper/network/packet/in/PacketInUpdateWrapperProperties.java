package eu.cloudnetservice.v2.wrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 30.09.2017.
 */
public class PacketInUpdateWrapperProperties implements PacketInHandler {

    private static final Type CONFIGURATION_TYPE = TypeToken.get(Configuration.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        Configuration configuration = packet.getData().getObject("configuration", CONFIGURATION_TYPE);

        // Merge configurations
        final Configuration wrapperConfig = CloudNetWrapper.getInstance().getWrapperConfig().getConfiguration();
        configuration.getKeys().forEach(key -> {
            wrapperConfig.set(key, configuration.get(key));
        });

        CloudNetWrapper.getInstance().getWrapperConfig().save();
        CloudNetWrapper.getInstance().getWrapperConfig().load();
    }
}
