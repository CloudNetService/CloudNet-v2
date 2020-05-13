package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * This event is called whenever a custom channel message has been received.
 * This event can be used to receive communication from servers across the entire network.
 */
public class ProxiedCustomChannelMessageReceiveEvent extends ProxiedCloudEvent {

    private String channel;

    private String message;

    private Document document;

    public ProxiedCustomChannelMessageReceiveEvent(String channel, String message, Document document) {
        this.channel = channel;
        this.message = message;
        this.document = document;
    }

    /**
     * @return the channel this channel message was sent on.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @return the document attached to this channel message.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return the message carried by this channel message.
     */
    public String getMessage() {
        return message;
    }
}
