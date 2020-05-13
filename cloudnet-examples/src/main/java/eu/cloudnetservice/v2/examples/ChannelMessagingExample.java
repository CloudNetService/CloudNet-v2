package eu.cloudnetservice.v2.examples;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.network.packet.out.PacketOutCustomChannelMessage;
import eu.cloudnetservice.v2.bridge.event.proxied.ProxiedSubChannelMessageEvent;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Tareko on 15.10.2017.
 */
public class ChannelMessagingExample {

    public void sendCustomMessage() {
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutCustomChannelMessage("some-sub-channel-for-proxy",
                                                                                                   "handle",
                                                                                                   new Document("foo",
                                                                                                                "bar"))); //send a custom channel message to all
        CloudAPI.getInstance().sendCustomSubProxyMessage("some-sub-channel-for-proxy",
                                                         "handle",
                                                         new Document("foo", "bar")); //send a custom channel message to all proxys
    }

    @EventHandler
    public void channelSubReceive(ProxiedSubChannelMessageEvent e) //handle the received channel message
    {
        if (e.getChannel().equalsIgnoreCase("some-sub-channel-for-proxy")) {
            if (e.getMessage().equalsIgnoreCase("handle")) {
                System.out.println(e.getDocument().convertToJson());
            }
        }
    }

}
