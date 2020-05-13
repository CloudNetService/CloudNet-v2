package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.utility.document.Document;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a custom channel message has been received.
 * This event can be used to receive communication from servers across the entire network.
 */
public class BukkitCustomChannelMessageReceiveEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private String channel;
    private String message;
    private Document document;

    public BukkitCustomChannelMessageReceiveEvent(String channel, String message, Document document) {
        super();
        this.channel = channel;
        this.message = message;
        this.document = document;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the message carried by this channel message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the document attached to this channel message.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return the channel this channel message was sent on.
     */
    public String getChannel() {
        return channel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
