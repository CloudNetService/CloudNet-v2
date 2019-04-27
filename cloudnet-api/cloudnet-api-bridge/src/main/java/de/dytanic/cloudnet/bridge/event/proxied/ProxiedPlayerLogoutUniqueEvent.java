/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 07.09.2017.
 */
@Getter
@AllArgsConstructor
public class ProxiedPlayerLogoutUniqueEvent extends ProxiedCloudEvent {

    private UUID uniqueId;

}