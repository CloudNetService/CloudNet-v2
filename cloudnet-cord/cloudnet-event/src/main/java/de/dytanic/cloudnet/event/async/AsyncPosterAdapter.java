/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.async;

public class AsyncPosterAdapter<E extends AsyncEvent<?>> implements AsyncPoster<E> {

    @Override
    public void onPreCall(E event)
    {
    }

    @Override
    public void onPostCall(E event)
    {
    }
}