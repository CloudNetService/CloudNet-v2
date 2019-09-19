/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;

import java.util.Collection;
import java.util.List;

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

    public ServerFallback getDefault() {
        return CollectionWrapper.filter(fallbacks, new Acceptable<ServerFallback>() {
            @Override
            public boolean isAccepted(ServerFallback serverFallback) {
                return serverFallback.getGroup().equals(defaultFallback);
            }
        });
    }

    public Collection<String> getNamedFallbackes() {
        return CollectionWrapper.transform(this.fallbacks, new Catcher<String, ServerFallback>() {
            @Override
            public String doCatch(ServerFallback key) {
                return key.getGroup();
            }
        });
    }

}
