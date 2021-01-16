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

package eu.cloudnetservice.cloudnet.v2.event;

import eu.cloudnetservice.cloudnet.v2.event.async.AsyncEvent;
import eu.cloudnetservice.cloudnet.v2.event.interfaces.IEventManager;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import net.jodah.typetools.TypeResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that manages events
 */
public final class EventManager implements IEventManager {

    private final Map<Class<? extends Event>, Collection<EventEntity>> registeredListeners = new ConcurrentHashMap<>();

    /**
     * Clears all currently registered {@link EventEntity}s from this event
     * manager instance.
     */
    public void clearAll() {
        this.registeredListeners.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> void registerListener(EventKey eventKey, EventListener<T> eventListener) {
        Class eventClazz = TypeResolver.resolveRawArgument(EventListener.class, eventListener.getClass());
        if (!registeredListeners.containsKey(eventClazz)) {
            registeredListeners.put(eventClazz, new ArrayList<>());
        }
        registeredListeners.get(eventClazz).add(new EventEntity<>(eventListener, eventKey, eventClazz));
    }

    @Override
    public final <T extends Event> void registerListeners(EventKey eventKey, EventListener<T>[] eventListeners) {
        for (EventListener<T> eventListener : eventListeners) {
            registerListener(eventKey, eventListener);
        }
    }

    @Override
    public void unregisterListener(EventKey eventKey) {
        for (Map.Entry<Class<? extends Event>, Collection<EventEntity>> eventEntities : this.registeredListeners.entrySet()) {
            for (EventEntity entities : eventEntities.getValue()) {
                if (entities.getEventKey().equals(eventKey)) {
                    registeredListeners.get(eventEntities.getKey()).remove(entities);
                }
            }
        }
    }

    @Override
    public void unregisterListener(EventListener<? extends Event> eventListener) {
        try {
            Class clazz = getClazz(eventListener);
            if (registeredListeners.containsKey(clazz)) {
                for (EventEntity eventEntity : registeredListeners.get(clazz)) {
                    if (eventEntity.getEventListener().equals(eventListener)) {
                        registeredListeners.get(clazz).remove(eventEntity);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterListener(Class<? extends Event> eventClass) {
        registeredListeners.remove(eventClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> boolean callEvent(T event) {
        if (!this.registeredListeners.containsKey(event.getClass())) {
            return true;
        }

        if (event instanceof AsyncEvent) {
            NetworkUtils.getExecutor().submit(() -> {
                AsyncEvent asyncEvent = ((AsyncEvent) event);
                asyncEvent.getPoster().onPreCall(asyncEvent);
                for (EventEntity eventEntity : registeredListeners.get(event.getClass())) {
                    eventEntity.getEventListener().onCall(event);
                }
                asyncEvent.getPoster().onPostCall(asyncEvent);
            });
        } else {
            for (EventEntity eventEntity : this.registeredListeners.get(event.getClass())) {
                eventEntity.getEventListener().onCall(event);
            }
        }
        return false;
    }

    private Class getClazz(EventListener<?> eventListener) throws Exception {
        return eventListener.getClass().getMethod("onCall", Event.class).getParameters()[0].getType();
    }


}
