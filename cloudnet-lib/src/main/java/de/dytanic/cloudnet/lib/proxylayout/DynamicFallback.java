/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;

import java.util.Collection;
import java.util.List;

/**
 * Created by Tareko on 05.10.2017.
 */
public class DynamicFallback {

    private final String defaultFallback;

    private final List<ServerFallback> fallbacks;

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

    public ServerFallback getDefault() {
        return fallbacks.stream()
                        .filter(fallback -> fallback.getGroup().equals(defaultFallback))
                        .findFirst().orElseThrow(() -> new IllegalStateException("No default fallback defined!"));
    }

    public Collection<String> getNamedFallbacks() {
        return CollectionWrapper.transform(this.fallbacks, new Catcher<String, ServerFallback>() {
            @Override
            public String doCatch(ServerFallback key) {
                return key.getGroup();
            }
        });
    }

}
