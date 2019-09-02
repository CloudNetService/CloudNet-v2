/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.interfaces;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.event.EventKey;
import de.dytanic.cloudnet.event.IEventListener;

/**
 * Interface for event managers
 */
public interface IEventManager {

    /**
     * Registers a new listener that will be called on matching events.
     *
     * @param eventKey      the domain of the event listener
     * @param eventListener the actual event listener instance that will be called upon
     * @param <T>           the type of the events to have the event listener be called on
     *
     * @see #registerListeners(EventKey, IEventListener[])
     */
    <T extends Event> void registerListener(EventKey eventKey, IEventListener<T> eventListener);

    /**
     * Registers multiple event listeners at once.
     *
     * @param eventKey       the domain of the event listeners
     * @param eventListeners the event listener instances that will be called upon
     * @param <T>            the type of the events to have the event listeners be called on
     *
     * @see #registerListener(EventKey, IEventListener)
     */
    <T extends Event> void registerListeners(EventKey eventKey, IEventListener<T>... eventListeners);

    /**
     * Removes all listener from a specific {@link EventKey} from this event manager.
     *
     * @param eventKey the domain of the event listeners that will be removed
     */
    void unregisterListener(EventKey eventKey);

    /**
     * Removes an event listener from all {@link de.dytanic.cloudnet.event.EventEntity}s.
     *
     * @param eventListener the event listener to remove
     */
    void unregisterListener(IEventListener<?> eventListener);

    /**
     * Removes all event listeners for a specific event class.
     *
     * @param eventClass the class that should have all its event listeners removed
     */
    void unregisterListener(Class<? extends Event> eventClass);

    /**
     * Call an event and forward it to all {@link IEventListener}s registered
     * on this event manager.
     *
     * @param event the event to call
     * @param <T>   the type of the event and {@link IEventListener} to call
     *
     * @return {@code true} when no {@link IEventListener} was called,
     * {@code false} otherwise
     */
    <T extends Event> boolean callEvent(T event);

}
