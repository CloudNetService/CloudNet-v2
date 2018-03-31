package de.dytanic.cloudnet.event;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.interfaces.IEventManager;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet3.TaskScheduler;
import net.jodah.typetools.TypeResolver;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Tareko on 23.07.2017.
 */
public final class EventManager implements IEventManager {

    private final java.util.Map<Class, Collection<EventEntity>> registeredListeners = NetworkUtils.newConcurrentHashMap();

    @Override
    public <T extends Event> void registerListener(EventKey eventKey, IEventListener<T> eventListener)
    {
        Class eventClazz = TypeResolver.resolveRawArgument(IEventListener.class, eventListener.getClass());
        if (!registeredListeners.containsKey(eventClazz))
        {
            registeredListeners.put(eventClazz, new LinkedList<>());
        }
        registeredListeners.get(eventClazz).add(new EventEntity(eventListener, eventKey, eventClazz));
    }

    @Override
    public <T extends Event> void registerListeners(EventKey eventKey, IEventListener<T>... eventListeners)
    {
        for (IEventListener<T> eventListener : eventListeners)
        {
            registerListener(eventKey, eventListener);
        }
    }

    @Override
    public void unregisterListener(EventKey eventKey)
    {
        for (Map.Entry<Class, Collection<EventEntity>> eventEntities : this.registeredListeners.entrySet())
        {
            for (EventEntity entities : eventEntities.getValue())
            {
                if (entities.getEventKey().equals(eventKey))
                    registeredListeners.get(eventEntities.getKey()).remove(entities);
            }
        }
    }

    @Override
    public void unregisterListener(IEventListener<?> eventListener)
    {
        try
        {
            Class clazz = getClazz(eventListener);
            if (registeredListeners.containsKey(clazz))
                for (EventEntity eventEntity : registeredListeners.get(clazz))
                    if (eventEntity.getEventListener().equals(eventListener))
                    {
                        registeredListeners.get(clazz).remove(eventEntity);
                        return;
                    }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterListener(Class<? extends Event> eventClass)
    {
        registeredListeners.remove(eventClass);
    }

    @Override
    public <T extends Event> boolean callEvent(T event)
    {
        if (!this.registeredListeners.containsKey(event.getClass())) return true;

        if (!(event instanceof AsyncEvent))
        {
            for (EventEntity eventEntity : this.registeredListeners.get(event.getClass()))
            {
                eventEntity.getEventListener().onCall(event);
            }
        } else
        {
            TaskScheduler.runtimeScheduler().schedule(new Runnable() {
                @Override
                public void run()
                {
                    AsyncEvent asyncEvent = ((AsyncEvent) event);
                    asyncEvent.getPoster().onPreCall(asyncEvent);
                    for (EventEntity eventEntity : registeredListeners.get(event.getClass()))
                    {
                        eventEntity.getEventListener().onCall(event);
                    }
                    asyncEvent.getPoster().onPostCall(asyncEvent);
                }
            });
        }
        return false;
    }

    private Class getClazz(IEventListener<?> eventListener) throws Exception
    {
        return eventListener.getClass().getMethod("onCall", Event.class).getParameters()[0].getType();
    }

    public void clearAll()
    {
        this.registeredListeners.clear();
    }
}