/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.web.server.handler.DynamicWebHandler;
import de.dytanic.cloudnet.web.server.handler.WebHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 14.09.2017.
 */
public final class WebServerProvider {

    private final Collection<WebHandler> handlers = new ConcurrentLinkedQueue<>();

    private final Collection<DynamicWebHandler> dynamicWebHandlers = new ConcurrentLinkedQueue<>();

    public WebServerProvider registerHandler(WebHandler httpHandler)
    {
        handlers.add(httpHandler);
        return this;
    }

    public WebServerProvider registerDynamicHandler(DynamicWebHandler dynamicWebHandler)
    {
        dynamicWebHandlers.add(dynamicWebHandler);
        return this;
    }

    public List<WebHandler> getHandlers()
    {
        return new ArrayList<>(handlers);
    }

    public List<WebHandler> getHandlers(String path)
    {
        return new ArrayList<>(CollectionWrapper.filterMany(handlers, new Acceptable<WebHandler>() {
            @Override
            public boolean isAccepted(WebHandler webHandler)
            {
                if((path.equals(NetworkUtils.SLASH_STRING) || path.isEmpty()) && webHandler.getPath().equals("/")) return true;

                String[] array = path.replaceFirst("/", NetworkUtils.EMPTY_STRING).split("/");
                String[] pathArray = webHandler.getPath().replaceFirst("/", NetworkUtils.EMPTY_STRING).split("/");

                if(array.length != pathArray.length) return false;

                for(short i = 0; i < array.length; i++)
                {
                    if(!((pathArray[i].startsWith("{") && pathArray[i].endsWith("}")) || pathArray[i].equals(array[i]))) return false;
                }
                return true;
            }
        }));
    }

    public void clear()
    {
        handlers.clear();
    }

}