/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.interfaces;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.event.EventKey;
import de.dytanic.cloudnet.event.IEventListener;

public interface IEventManager {

    <T extends Event> void registerListener(EventKey eventKey, IEventListener<T> eventListener);

    <T extends Event> void registerListeners(EventKey eventKey, IEventListener<T>...eventListeners);

    void unregisterListener(EventKey eventKey);

    void unregisterListener(IEventListener<?> eventListener);

    void unregisterListener(Class<? extends Event> eventClass);

    <T extends Event> boolean callEvent(T event);

}