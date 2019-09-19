/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
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

    public String getChannel() {
        return channel;
    }

    public Document getDocument() {
        return document;
    }

    public String getMessage() {
        return message;
    }
}
