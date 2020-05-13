package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * This event is called when a sub channel message has been received by this proxy server.
 */
public class ProxiedSubChannelMessageEvent extends ProxiedCloudEvent {

    private String channel;

    private String message;

    private Document document;

    public ProxiedSubChannelMessageEvent(String channel, String message, Document document) {
        this.channel = channel;
        this.message = message;
        this.document = document;
    }

    /**
     * @return the channel through which this message was sent.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @return the attached document to the message.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return the message that was sent.
     */
    public String getMessage() {
        return message;
    }
}
