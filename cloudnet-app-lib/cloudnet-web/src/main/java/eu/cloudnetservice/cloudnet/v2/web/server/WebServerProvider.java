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

package eu.cloudnetservice.cloudnet.v2.web.server;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.web.server.handler.DynamicWebHandler;
import eu.cloudnetservice.cloudnet.v2.web.server.handler.WebHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Class that provides web handlers and dynamic web handlers for a {@link WebServer}.
 */
public final class WebServerProvider {

    private final Collection<WebHandler> handlers = new ConcurrentLinkedQueue<>();

    private final Collection<DynamicWebHandler> dynamicWebHandlers = new ConcurrentLinkedQueue<>();

    /**
     * Adds a new web handler to this provider
     *
     * @param httpHandler the web handler to add
     *
     * @return this provider
     */
    public WebServerProvider registerHandler(WebHandler httpHandler) {
        handlers.add(httpHandler);
        return this;
    }

    /**
     * Adds a new dynamic web handler to this provider
     *
     * @param dynamicWebHandler the dynamic web handler to add
     *
     * @return this provider
     */
    public WebServerProvider registerDynamicHandler(DynamicWebHandler dynamicWebHandler) {
        dynamicWebHandlers.add(dynamicWebHandler);
        return this;
    }

    /**
     * Constructs a new list of all currently registered web handlers.
     * Modifications to the list itself don't affect this provider, but
     * modifications to the stored web handlers do.
     *
     * @return a new list containing the currently registered web handlers
     */
    public List<WebHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    /**
     * Filters the currently registered web handlers by their path and returns
     * a new list.
     * <p>
     * Modifications to the list itself don't affect this provider, but
     * modifications to the stored web handlers do.
     *
     * @param path the path to filter the registered web handlers by
     *
     * @return a new list containing the currently registered web handlers
     */
    public List<WebHandler> getHandlers(String path) {
        return handlers.stream().filter(webHandler -> {
            if ((path.equals(NetworkUtils.SLASH_STRING) || path.isEmpty()) && webHandler.getPath().equals("/")) {
                return true;
            }

            String[] array = path.replaceFirst("/", NetworkUtils.EMPTY_STRING).split("/");
            String[] pathArray = webHandler.getPath().replaceFirst("/", NetworkUtils.EMPTY_STRING).split("/");

            if (array.length != pathArray.length) {
                return false;
            }

            for (short i = 0; i < array.length; i++) {
                if (!((pathArray[i].startsWith("{") && pathArray[i].endsWith("}")) || pathArray[i].equals(array[i]))) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Constructs a new list of all currently registered dynamic web handlers.
     * Modifications to the list itself don't affect this provider, but
     * modifications to the stored dynamic web handlers do.
     *
     * @return a new list containing the currently registered dynamic web handlers
     */
    public List<DynamicWebHandler> getDynamicHandlers() {
        return new ArrayList<>(dynamicWebHandlers);
    }

    /**
     * Clears the currently registered web handlers and dynamic web handlers.
     */
    public void clear() {
        handlers.clear();
        dynamicWebHandlers.clear();
    }

}
