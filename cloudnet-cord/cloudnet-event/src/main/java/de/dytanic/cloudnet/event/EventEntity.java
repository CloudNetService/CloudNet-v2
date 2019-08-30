/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event;

/**
 * Class that defines an entity that handles events of a defined type
 */
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

    public EventEntity(IEventListener<E> eventListener, EventKey eventKey, Class<? extends Event> eventClazz) {
        this.eventListener = eventListener;
        this.eventKey = eventKey;
        this.eventClazz = eventClazz;
    }

    public Class<? extends Event> getEventClazz() {
        return eventClazz;
    }

    public EventKey getEventKey() {
        return eventKey;
    }

    public IEventListener<E> getEventListener() {
        return eventListener;
    }
}
