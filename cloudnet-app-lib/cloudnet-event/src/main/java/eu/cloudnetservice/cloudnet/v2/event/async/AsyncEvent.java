/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.event.async;

import eu.cloudnetservice.cloudnet.v2.event.Event;

/**
 * Interface for asynchronous events
 *
 * @param <E> the type of the asynchronous events
 */
public abstract class AsyncEvent<E extends AsyncEvent<?>> extends Event {

    private final AsyncPoster<E> poster;

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
