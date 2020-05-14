package eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit;

import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a sub channel message has been received by this Bukkit server.
 */
public class BukkitSubChannelMessageEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final String channel;

    private final String message;

    private final Document document;

    public BukkitSubChannelMessageEvent(String channel, String message, Document document) {
        this.channel = channel;
        this.message = message;
        this.document = document;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the message that was sent.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the attached document to the message.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return the channel through which this message was sent.
     */
    public String getChannel() {
        return channel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
