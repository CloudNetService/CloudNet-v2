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

package eu.cloudnetservice.cloudnet.v2.event;

/**
 * Class that defines an entity that handles events of a defined type
 */
public class EventEntity<E extends Event> {

    /**
     * The event listener that is called for events of the class {@link #eventClazz}
     */
    private final EventListener<E> eventListener;

    private final EventKey eventKey;

    /**
     * Subclass of {@link Event} this entity should listen to.
     */
    private final Class<? extends Event> eventClazz;

    public EventEntity(EventListener<E> eventListener, EventKey eventKey, Class<? extends Event> eventClazz) {
        this.eventListener = eventListener;
        this.eventKey = eventKey;
        this.eventClazz = eventClazz;
    }

    public Class<? extends Event> getEventClazz() {
        return eventClazz;
    }

    public EventKey getEventKey() {
        return eventKey;
    }

    public EventListener<E> getEventListener() {
        return eventListener;
    }
}
