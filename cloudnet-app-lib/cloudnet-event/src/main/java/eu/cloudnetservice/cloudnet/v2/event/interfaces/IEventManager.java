/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
