/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.async;

import de.dytanic.cloudnet.event.Event;
import lombok.Getter;

/**
 * Asynchronized Event server
 *
 * @param <E>
 */
@Getter
public abstract class AsyncEvent<E extends AsyncEvent<?>> extends Event {

    public AsyncEvent(AsyncPoster<E> poster)
    {
        this.poster = poster;
        this.asynchronized = true;
    }

    private AsyncPoster<E> poster;

}