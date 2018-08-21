/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class that defines an entity that handles events of a defined type
 */
@Getter
@AllArgsConstructor
public class EventEntity<E extends Event> {

    /**
     * The event listener that is called for events of the class {@link #eventClazz}
     */
    private IEventListener<E> eventListener;

    private EventKey eventKey;

    /**
     * Subclass of {@link Event} this entity should listen to.
     */
    private Class<? extends Event> eventClazz;

}
