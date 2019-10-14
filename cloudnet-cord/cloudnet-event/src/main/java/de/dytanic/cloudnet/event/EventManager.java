package de.dytanic.cloudnet.event;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.interfaces.IEventManager;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import net.jodah.typetools.TypeResolver;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class that manages events
 */
public final class EventManager implements IEventManager {

    private final Map<Class<? extends Event>, Collection<EventEntity>> registeredListeners = NetworkUtils.newConcurrentHashMap();

    /**
     * Clears all currently registered {@link EventEntity}s from this event
     * manager instance.
     */
    public void clearAll() {
        this.registeredListeners.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> void registerListener(EventKey eventKey, IEventListener<T> eventListener) {
        Class eventClazz = TypeResolver.resolveRawArgument(IEventListener.class, eventListener.getClass());
        if (!registeredListeners.containsKey(eventClazz)) {
            registeredListeners.put(eventClazz, new LinkedList<>());
        }
        registeredListeners.get(eventClazz).add(new EventEntity<>(eventListener, eventKey, eventClazz));
    }

    @SafeVarargs
    @Override
    public final <T extends Event> void registerListeners(EventKey eventKey, IEventListener<T>... eventListeners) {
        for (IEventListener<T> eventListener : eventListeners) {
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
    public void unregisterListener(IEventListener<? extends Event> eventListener) {
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

        if (!(event instanceof AsyncEvent)) {
            for (EventEntity eventEntity : this.registeredListeners.get(event.getClass())) {
                eventEntity.getEventListener().onCall(event);
            }
        } else {
            TaskScheduler.runtimeScheduler().schedule(() -> {
                AsyncEvent asyncEvent = ((AsyncEvent) event);
                asyncEvent.getPoster().onPreCall(asyncEvent);
                for (EventEntity eventEntity : registeredListeners.get(event.getClass())) {
                    eventEntity.getEventListener().onCall(event);
                }
                asyncEvent.getPoster().onPostCall(asyncEvent);
            });
        }
        return false;
    }

    private Class getClazz(IEventListener<?> eventListener) throws Exception {
        return eventListener.getClass().getMethod("onCall", Event.class).getParameters()[0].getType();
    }


}
