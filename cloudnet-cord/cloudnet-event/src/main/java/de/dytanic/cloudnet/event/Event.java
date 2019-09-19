package de.dytanic.cloudnet.event;

/**
 * Abstract class for events of all kinds.
 */
public abstract class Event {

    /**
     * Whether this event will be called asynchronous
     */
    protected boolean asynchronous = false;

    public boolean isAsynchronous() {
        return asynchronous;
    }
}
