/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.event.async;

public interface AsyncPoster<E extends AsyncEvent> {

    void onPreCall(E event);

    void onPostCall(E event);

}