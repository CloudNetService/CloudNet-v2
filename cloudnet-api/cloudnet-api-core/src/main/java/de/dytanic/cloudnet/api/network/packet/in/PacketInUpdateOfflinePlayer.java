package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.function.Consumer;

public final class PacketInUpdateOfflinePlayer extends PacketInHandlerDefault {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        OfflinePlayer offlinePlayer = data.getObject("player", OfflinePlayer.TYPE);

        if(CloudAPI.getInstance() != null)
        {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Consumer<NetworkHandler>() {
                @Override
                public void accept(NetworkHandler obj)
                {
                    obj.onOfflinePlayerUpdate(offlinePlayer);
                }
            });
        }
    }
}