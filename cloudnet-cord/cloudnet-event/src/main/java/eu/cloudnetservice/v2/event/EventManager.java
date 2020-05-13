package eu.cloudnetservice.v2.event;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.interfaces.IEventManager;
import eu.cloudnetservice.v2.lib.NetworkUtils;
import net.jodah.typetools.TypeResolver;

import java.util.Collection;
import java.util.LinkedList;
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
            registeredListeners.put(eventClazz, new LinkedList<>());
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
