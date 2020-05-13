package eu.cloudnetservice.v2.event;

/**
 * Class that defines an entity that handles events of a defined type
 */
public class EventEntity<E extends Event> {

    /**
     * The event listener that is called for events of the class {@link #eventClazz}
     */
    private final EventListener<E> eventListener;

    private final EventKey eventKey;

    /**
     * Subclass of {@link Event} this entity should listen to.
     */
    private final Class<? extends Event> eventClazz;

    public EventEntity(EventListener<E> eventListener, EventKey eventKey, Class<? extends Event> eventClazz) {
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

    public EventListener<E> getEventListener() {
        return eventListener;
    }
}
