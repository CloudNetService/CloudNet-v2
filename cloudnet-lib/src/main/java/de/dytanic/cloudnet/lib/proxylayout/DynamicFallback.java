/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 05.10.2017.
 */
public class DynamicFallback {

    private String defaultFallback;

    private List<ServerFallback> fallbacks;

    public DynamicFallback(String defaultFallback, List<ServerFallback> fallbacks) {
        this.defaultFallback = defaultFallback;
        this.fallbacks = fallbacks;
    }

    public List<ServerFallback> getFallbacks() {
        return fallbacks;
    }

    public String getDefaultFallback() {
        return defaultFallback;
    }

    public ServerFallback getDefault()
    {
        return fallbacks.stream().filter(serverFallback -> serverFallback.getGroup().equals(defaultFallback)).findFirst().orElse(null);
    }

    public Collection<String> getNamedFallbackes()
    {
        return this.fallbacks.stream().map(ServerFallback::getGroup).collect(Collectors.toList());
    }

}