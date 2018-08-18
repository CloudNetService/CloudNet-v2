/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.async;

/**
 * Adapter for {@link AsyncPoster}
 * Provides empty method bodies for {@link AsyncPoster#onPreCall(AsyncEvent)}
 * and {@link AsyncPoster#onPostCall(AsyncEvent)}
 *
 * @param <E> the type of the event
 */
public class AsyncPosterAdapter<E extends AsyncEvent<?>> implements AsyncPoster<E> {

    @Override
    public void onPreCall(E event) {
    }

    @Override
    public void onPostCall(E event) {
    }
}
