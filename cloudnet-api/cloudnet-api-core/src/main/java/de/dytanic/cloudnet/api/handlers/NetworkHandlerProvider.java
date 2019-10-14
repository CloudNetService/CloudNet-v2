/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.handlers;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Managed all NetworkHandlers an you can registerd new NetworkHandler implmentations
 */
public class NetworkHandlerProvider {

    private Collection<NetworkHandler> handlers = new CopyOnWriteArrayList<>();

    /**
     * Registerd a network Handler
     *
     * @param networkHandler
     */
    public void registerHandler(NetworkHandler networkHandler) {
        this.handlers.add(networkHandler);
    }

    /**
     * Iteration of all registed Handlers
     *
     * @param handlerTask
     */
    public void iterator(Runnabled<NetworkHandler> handlerTask) {
        CollectionWrapper.iterator(handlers, handlerTask);
    }

    public void clear() {
        handlers.clear();
    }
}
