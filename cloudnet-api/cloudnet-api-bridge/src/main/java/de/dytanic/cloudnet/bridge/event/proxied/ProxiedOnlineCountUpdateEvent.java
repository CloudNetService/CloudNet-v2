/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Tareko on 11.10.2017.
 */
@Getter
@AllArgsConstructor
public class ProxiedOnlineCountUpdateEvent extends Event {

    private int onlineCount;

}