package eu.cloudnetservice.cloudnet.v2.event.interfaces;

import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.event.EventEntity;
import eu.cloudnetservice.cloudnet.v2.event.EventKey;
import eu.cloudnetservice.cloudnet.v2.event.EventListener;

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
     * @see #registerListeners(EventKey, EventListener[])
     */
    <T extends Event> void registerListener(EventKey eventKey, EventListener<T> eventListener);

    /**
     * Registers multiple event listeners at once.
     *
     * @param eventKey       the domain of the event listeners
     * @param eventListeners the event listener instances that will be called upon
     * @param <T>            the type of the events to have the event listeners be called on
     *
     * @see #registerListener(EventKey, EventListener)
     */
    <T extends Event> void registerListeners(EventKey eventKey, EventListener<T>[] eventListeners);

    /**
     * Removes all listener from a specific {@link EventKey} from this event manager.
     *
     * @param eventKey the domain of the event listeners that will be removed
     */
    void unregisterListener(EventKey eventKey);

    /**
     * Removes an event listener from all {@link EventEntity}s.
     *
     * @param eventListener the event listener to remove
     */
    void unregisterListener(EventListener<?> eventListener);

    /**
     * Removes all event listeners for a specific event class.
     *
     * @param eventClass the class that should have all its event listeners removed
     */
    void unregisterListener(Class<? extends Event> eventClass);

    /**
     * Call an event and forward it to all {@link EventListener}s registered
     * on this event manager.
     *
     * @param event the event to call
     * @param <T>   the type of the event and {@link EventListener} to call
     *
     * @return {@code true} when no {@link EventListener} was called,
     * {@code false} otherwise
     */
    <T extends Event> boolean callEvent(T event);

}
