package de.dytanic.cloudnet.event;

import lombok.Getter;

/**
 * Abstract class for events of all kinds.
 */
@Getter
public abstract class Event {

    /**
     * Whether this event will be called asynchronous
     */
    protected boolean asynchronous = false;

}
