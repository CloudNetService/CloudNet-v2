package eu.cloudnetservice.cloudnet.v2.event;

/**
 * Interface for marking event listeners.
 */
public interface EventListener<E extends Event> {

    /**
     * Method that is called on an event listener to handle an event of type
     * {@code E}
     *
     * @param event the event that this listener can handle
     */
    void onCall(E event);

}
