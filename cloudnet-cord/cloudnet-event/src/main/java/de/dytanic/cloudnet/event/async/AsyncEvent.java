/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.async;

import de.dytanic.cloudnet.event.Event;

/**
 * Interface for asynchronous events
 *
 * @param <E> the type of the asynchronous events
 */
public abstract class AsyncEvent<E extends AsyncEvent<?>> extends Event {

    private AsyncPoster<E> poster;

    /**
     * Constructs a new asynchronous event with an asynchronous poster.
     *
     * @param poster the poster to handle pre- and post-call methods
     */
    public AsyncEvent(AsyncPoster<E> poster) {
        this.poster = poster;
        this.asynchronous = true;
    }

    public AsyncPoster<E> getPoster() {
        return poster;
    }
}
