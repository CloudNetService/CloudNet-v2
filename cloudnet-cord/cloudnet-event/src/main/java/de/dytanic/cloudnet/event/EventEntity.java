/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventEntity {

    private IEventListener eventListener;

    private EventKey eventKey;

    private Class<? extends Event> eventClazz;

}