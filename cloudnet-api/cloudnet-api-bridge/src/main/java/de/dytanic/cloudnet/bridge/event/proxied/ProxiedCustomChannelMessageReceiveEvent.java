/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;

/**
 * Calls if a custom channel message was received
 */
@AllArgsConstructor
public class ProxiedCustomChannelMessageReceiveEvent extends ProxiedCloudEvent {

    private String channel;

    private String message;

    private Document document;

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