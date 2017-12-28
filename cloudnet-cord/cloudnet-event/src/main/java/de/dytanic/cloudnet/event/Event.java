package de.dytanic.cloudnet.event;

import lombok.Getter;

/**
 * Basic Event Clazz
 */
@Getter
public abstract class Event {

    protected boolean asynchronized = false;

}