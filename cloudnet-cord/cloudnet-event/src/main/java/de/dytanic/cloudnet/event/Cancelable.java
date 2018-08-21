package de.dytanic.cloudnet.event;

/**
 * Interface for cancelable tasks or events.
 */
public interface Cancelable {

    /**
     * Returns whether this event is canceled.
     *
     * @return whether this event is canceled
     */
    boolean isCancelled();

    /**
     * Sets the current event to be canceled.
     * It is up to the event handler to honor this.
     *
     * @param cancel whether this event is canceled
     */
    void setCancelled(boolean cancel);

}
