package de.dytanic.cloudnet.api.handlers;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

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
    public void iterator(Consumer<NetworkHandler> handlerTask) {
        handlers.forEach(handlerTask);
    }

    public void clear() {
        handlers.clear();
    }
}
